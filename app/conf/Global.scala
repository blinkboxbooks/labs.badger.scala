package conf

import play.api.libs.concurrent.Akka
import play.api.{Play, Logger, Application, GlobalSettings}
import services._
import play.api.Play.current

object Global extends GlobalSettings{
  private var _badgesDb: BadgesDB = _
  private var _users: UserService = _
  private var _eventsDb: EventsDB = _

  override def onStart(app: Application): Unit = {
    val configuration = current.configuration
    _badgesDb = new MongoBadgesDB
    _users = new MongoUserService(badgesDb)
    _eventsDb = new CassandraEventsDB(configuration.underlying)
    new MessagingContext(Akka.system, configuration.underlying)
    Logger.info("Badger started.")
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Badger stopped.")
  }

  def badgesDb = _badgesDb
  def users = _users
  def eventsDb = _eventsDb
}
