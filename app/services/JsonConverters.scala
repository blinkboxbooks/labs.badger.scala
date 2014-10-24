package services

import models.{DisplayableBadge, UserData, Category, Badge}
import play.api.libs.json.{Json, Writes}

trait JsonConverters {
  implicit val categoryWriters = new Writes[Category] {
    override def writes(category: Category) = Json.obj(
      "id" -> category.id,
      "name" -> category.name
    )
  }

  implicit val badgesWrites = new Writes[Badge] {
    override def writes(badge: Badge) = Json.obj(
      "name" -> badge.name,
      "description" -> badge.description,
      "status" -> badge.status,
      "category" -> badge.category.name
    )
  }

  implicit val displayableBadgeWrites = new Writes[DisplayableBadge] {
    override def writes(badge: DisplayableBadge) = {
      val obj = Json.obj(
        "name" -> badge.name,
        "description" -> badge.description,
        "owned" -> badge.owned
      )
      badge.badgeUri.map(uri => obj ++ Json.obj("badgeUri" -> uri)).getOrElse(obj)
    }
  }

  implicit val userDataWrites = new Writes[UserData] {
    override def writes(userData: UserData) = Json.obj(
      "name" -> userData.name,
      "badges" -> userData.badges
    )
  }
}
