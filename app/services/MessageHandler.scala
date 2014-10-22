package services

import akka.actor.ActorRef
import com.blinkbox.books.messaging.{Event, ReliableEventHandler, ErrorHandler}
import conf.Global
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
    } yield {
      println(s"Got events: ${events.size}")
      val booksFinished = eventNames.filter(_.startsWith("BookFinished-")).map{
        finishedEvent =>
          (finishedEvent, events.filter(_.name == finishedEvent).size)
      }
      println(s"Got event: $booksFinished")
    }
  }

  override protected[this] def isTemporaryFailure(e: Throwable): Boolean = false
}

