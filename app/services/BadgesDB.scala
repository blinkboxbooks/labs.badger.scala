package services

import models._
import scala.concurrent.Future

trait BadgesDB {
  def publicBadges: Future[List[Badge]]
  def saveBadge(badgePerUser: UserBadge): Future[Unit]
  def badges(userId: Long): Future[List[UserBadge]]
  def bisac(bisacId: String): Future[Option[Bisac]]
  def rule(bisac: Bisac): Future[Option[Rule]]
}
