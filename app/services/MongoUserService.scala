package services

import models.UserData
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MongoUserService(db: BadgesDB) extends UserService{
  override def userStatus(userId: String): Future[UserData] = {
    for{
      userBadges <- db.badges(userId)
      badges <- db.publicBadges
    } yield {
      val earnedBadges = userBadges.map(_.badge)
      val notEarnedBadges = badges.filterNot(badge => earnedBadges.map(_.name).contains(badge.name))
      UserData(earnedBadges, notEarnedBadges)
    }
  }
}
