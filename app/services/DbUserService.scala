package services

import models.{DisplayableBadge, UserData}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DbUserService(db: BadgesDB) extends UserService{
  override def userStatus(userId: Long): Future[List[UserData]] = {
    for{
      userBadges <- db.badges(userId)
      badges <- db.publicBadges
    } yield {
      val ownedBadges = userBadges.map(_.badge.name)
      badges
        .groupBy(_.category)
        .map{ case (category, groupedBadges) =>
          val displayableBadges = groupedBadges.map{ badge =>
            val ownedBadge = ownedBadges.contains(badge.name)
            val badgeUri = if(ownedBadge) Some(badge.uri) else Option.empty[String]
            DisplayableBadge(badge.name, badge.description, ownedBadge, badgeUri)
          }
          UserData(category.name, displayableBadges)
        }.toList
    }
  }
}
