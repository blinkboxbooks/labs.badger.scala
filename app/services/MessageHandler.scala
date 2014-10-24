package services

import akka.actor.ActorRef
import com.blinkbox.books.messaging.{Event, ReliableEventHandler, ErrorHandler}
import conf.Global
import models.{Event => ReadEvent, Badge, UserBadge, Rule, BisacsRead}
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class MessageHandler(errorHandler: ErrorHandler, retryInterval: FiniteDuration) extends ReliableEventHandler(errorHandler, retryInterval) {
  private val eventsDb = Global.eventsDb
  private val badgesDb = Global.badgesDb

  override protected[this] def handleEvent(event: Event, originalSender: ActorRef): Future[Unit] = {
    val pendingEvent = Json.parse(event.body.asString())
    val eventUser = (pendingEvent \ "user_id").as[Long]
    val eventNames = (pendingEvent \ "names").as[List[String]]
    for{
      events <- eventsDb.events(eventUser)
      userBadges <- badgesDb.badges(eventUser)
      booksFinished = booksFinishedByEvent(eventUser, eventNames, events)
      booksRead <- asBisacs(booksFinished)
      rulesPerBisac <- rulesForBisacs(booksRead)
    } yield {
      Logger.debug(s"Events -> $events")
      Logger.debug(s"Rules -> $rulesPerBisac")
      rulesPerBisac.foreach{ rulePerBisac =>
        val (bisacRead, rule) = rulePerBisac
        val earnBadge = bisacRead.count >= rule.booksCount
        val alreadyEarnedBadge = userBadges.map(_.badge).contains(rule.badge)
        if(earnBadge && !alreadyEarnedBadge)
          badgesDb.saveBadge(UserBadge(eventUser, "TestingUser", rule.badge))
      }
    }
  }

  override protected[this] def isTemporaryFailure(e: Throwable): Boolean = false

  private def booksFinishedByEvent(user: Long, eventNames: List[String], events: List[ReadEvent]): List[(String, Int)] = {
    eventNames
      .filter(_.startsWith("BookFinished-"))
      .map(eventName => (eventName, events.count(_.name == eventName)))
  }

  private def asBisacs(eventsCount: List[(String, Int)]): Future[List[BisacsRead]] =
    Future.sequence{
      eventsCount.map{
        case (eventName, count) =>
          badgesDb.bisac(eventName.replace("BookFinished-", ""))
            .map(_.map(bisac => BisacsRead(bisac, count)))
      }
    }.map(_.flatten)

  private def rulesForBisacs(bisacs: List[BisacsRead]): Future[List[(BisacsRead, Rule)]] =
    Future.sequence{
      bisacs.map{
        bisac =>
          badgesDb.rule(bisac.bisac).map(_.map(rule => (bisac, rule)))
      }
    }.map(_.flatten)
}
