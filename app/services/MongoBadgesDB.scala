package services

import models.{BadgeCategory, BadgePerUser, Badge}
import play.api.Play
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.MongoDriver
import reactivemongo.bson.BSONDocument
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MongoBadgesDB extends BadgesDB with Converters{
  private val conf = Play.current.configuration
  private val driver = new MongoDriver
  private val dbHost = conf.getString("mongo.badges.host").getOrElse("localhost")
  private val connection = driver.connection(List(dbHost))
  private val db = connection(conf.getString("mongo.badges.db").getOrElse("badges"))
  private val BadgesCollection: BSONCollection = db("badges")
  private val UsersCollection: BSONCollection = db("userbadges")
  private val BadgeCategoriesCollection: BSONCollection = db("categories")

  override def publicBadges: Future[List[Badge]] = BadgesCollection.find(
    BSONDocument("status" -> "public")
  ).cursor[Badge].collect[List]()

  override def saveBadge(badgePerUser: BadgePerUser): Future[Unit] = {
    UsersCollection.save(badgePerUser).map(_ => ())
  }

  override def badgeCategories: Future[List[BadgeCategory]] = {
    BadgeCategoriesCollection
      .find(BSONDocument.empty)
      .cursor[BadgeCategory]
      .collect[List]()
  }

  override def badges(userId: String): Future[List[BadgePerUser]] = {
    val query = BSONDocument(
      "user.id" -> userId
    )
    UsersCollection
      .find(query)
      .cursor[BadgePerUser]
      .collect[List]()
  }
}
