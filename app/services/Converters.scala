package services

import models._
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader}

trait Converters {
  implicit val badgeCategoryReader = new BSONDocumentReader[BadgeCategory] {
    override def read(bson: BSONDocument): BadgeCategory = BadgeCategory(
      code = bson.getAs[String]("code").get,
      label = bson.getAs[String]("label").get
    )
  }

  implicit val badgeCategoryWriter = new BSONDocumentWriter[BadgeCategory] {
    override def write(badgeCategory: BadgeCategory): BSONDocument = BSONDocument(
      "code" -> badgeCategory.code,
      "label" -> badgeCategory.label
    )
  }

  implicit val badgeReader = new BSONDocumentReader[Badge] {
    override def read(bson: BSONDocument): Badge = Badge(
      name = bson.getAs[String]("name").get,
      label = bson.getAs[String]("label").get,
      level = BadgeLevel(bson.getAs[String]("level").get),
      category = bson.getAs[BadgeCategory]("category").get,
      status = BadgeStatus(bson.getAs[String]("status").get)
    )
  }

  implicit val badgeWriter = new BSONDocumentWriter[Badge] {
    override def write(badge: Badge): BSONDocument = BSONDocument(
      "name" -> badge.name,
      "label" -> badge.label,
      "level" -> badge.level.value,
      "category" -> badge.category,
      "status" -> badge.status.value
    )
  }

  implicit val userWriter = new BSONDocumentWriter[User] {
    override def write(user: User): BSONDocument = BSONDocument(
      "id" -> user.id,
      "name" -> user.name
    )
  }

  implicit val userReader = new BSONDocumentReader[User] {
    override def read(bson: BSONDocument): User = User(
      id = bson.getAs[String]("id").get,
      name = bson.getAs[String]("name").get
    )
  }

  implicit val badgePerUserReader = new BSONDocumentReader[BadgePerUser] {
    override def read(bson: BSONDocument): BadgePerUser = BadgePerUser(
      badge = bson.getAs[Badge]("badge").get,
      user = bson.getAs[User]("user").get
    )
  }

  implicit val badgePerUserWriter = new BSONDocumentWriter[BadgePerUser] {
    override def write(badgePerUser: BadgePerUser): BSONDocument = BSONDocument(
      "badge" -> badgePerUser.badge,
      "user" -> badgePerUser.user
    )
  }
}
