package conf

import java.util.concurrent.Executors

import play.api.libs.concurrent.Akka
import play.api.{Logger, Application, GlobalSettings}
import services._
import play.api.Play.current

import scala.concurrent.{Future, ExecutionContext}

object Global extends GlobalSettings{
  private var _badgesDb: BadgesDB = _
  private var _users: UserService = _
  private var _eventsDb: EventsDB = _

  override def onStart(app: Application): Unit = {
    initBadgesDb()
    initEventsDb()
    initUsersService()
    initMessaging()
    Logger.info("Badger started.")
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Badger stopped.")
  }

  private def initBadgesDb(): Unit = {
    _badgesDb = new JdbcBadgesDB
    Logger.info("DB connection started.")
  }

  private def initEventsDb(): Unit = {
    implicit val configuration = current.configuration
    _eventsDb = new CassandraEventsDB(configuration.underlying)
    Logger.info("EventsDB connection started.")
  }

  private def initUsersService(): Unit = {
    _users = new DbUserService(badgesDb)
  }

  private def initMessaging(): Unit = {
    implicit val configuration = current.configuration
    implicit val msgEc = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))
    val messaging = Future{
      new MessagingContext(Akka.system, configuration.underlying)
      Logger.info("Messaging connection started.")
    }
    messaging.onFailure{
      case e => Logger.error("Something happened with messaging", e)
    }
  }

  def badgesDb = _badgesDb
  def users = _users
  def eventsDb = _eventsDb
}
