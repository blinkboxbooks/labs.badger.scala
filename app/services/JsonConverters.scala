package services

import models.{UserData, BadgeCategory, Badge}
import play.api.libs.json.{Json, Writes}

trait JsonConverters {
  implicit val categoryWriters = new Writes[BadgeCategory] {
    override def writes(category: BadgeCategory) = Json.obj(
      "code" -> category.code,
      "label" -> category.label
    )
  }

  implicit val badgesWrites = new Writes[Badge] {
    override def writes(badge: Badge) = Json.obj(
      "name" -> badge.name
    )
  }

  implicit val userDataWrites = new Writes[UserData] {
    override def writes(userData: UserData) = Json.obj(
      "earned" -> userData.earnedBadges,
      "canEarn" -> userData.canEarn
    )
  }
}
