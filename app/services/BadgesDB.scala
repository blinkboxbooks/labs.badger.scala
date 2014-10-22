package services

import models.{BadgeCategory, BadgePerUser, Badge}

import scala.concurrent.Future

trait BadgesDB {
  def publicBadges: Future[List[Badge]]
  def saveBadge(badgePerUser: BadgePerUser): Future[Unit]
  def badgeCategories: Future[List[BadgeCategory]]
  def badges(userId: String): Future[List[BadgePerUser]]
}
