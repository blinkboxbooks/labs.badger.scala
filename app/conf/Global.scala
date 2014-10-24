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
    val configuration = current.configuration
    _badgesDb = new JdbcBadgesDB
    Logger.info("DB connection started.")
    _eventsDb = new CassandraEventsDB(configuration.underlying)
    Logger.info("EventsDB connection started.")
    _users = new DbUserService(badgesDb)
    val messagingEc = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))
    val messaging = Future{
      new MessagingContext(Akka.system, configuration.underlying)
      Logger.info("Messaging connection started.")
    }(messagingEc)
    messaging.onFailure{
      case e => Logger.error("Something happened with messaging", e)
    }(messagingEc)
    Logger.info("Badger started.")
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Badger stopped.")
  }

  def badgesDb = _badgesDb
  def users = _users
  def eventsDb = _eventsDb
}
