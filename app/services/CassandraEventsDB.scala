package services

import java.util.Date

import com.datastax.driver.core.Cluster
import com.typesafe.config.Config
import eu.inn.binders.cassandra.SessionQueryCache
import eu.inn.binders.naming.PlainConverter
import models.Event
import org.joda.time.DateTime
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CassandraEventsDB(conf: Config) extends EventsDB{

  private object DefaultCassandraManager {
    val port = conf.getInt("cassandra.port")
    val host = conf.getString("cassandra.host")

    lazy val cluster: Cluster = Cluster.builder()
      .addContactPoint(host)
      .withPort(port)
      .build()

    def session = cluster.connect(conf.getString("cassandra.keyspace"))
  }

  val session = DefaultCassandraManager.session
  case class CassandraEvent(event_id: String, user_id: Long, name: String, occurred_at: Date)

  override def events(userId: Long): Future[List[Event]] = {
    implicit val cache = new SessionQueryCache[PlainConverter](session)
    import eu.inn.binders.cassandra._
    cql"select * from events where user_id = $userId ALLOW FILTERING".all[CassandraEvent].map{events =>
      events.map{
        cassandraEvent => Event(
          cassandraEvent.event_id,
          cassandraEvent.user_id,
          cassandraEvent.name,
          new DateTime(cassandraEvent.occurred_at))
      }.toList
    }
  }
}
