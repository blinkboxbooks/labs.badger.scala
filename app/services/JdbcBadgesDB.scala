package services

import models._
import scala.concurrent.Future
import anorm._
import play.api.db.DB

class JdbcBadgesDB extends BadgesDB with Sqls{
  import play.api.Play.current
  import anorm.SqlParser._

  private val badgesParser =
    str("badgeName") ~ str("badgeDescription") ~ str("badgeStatus") ~
    int("categoryId") ~ str("categoryName") ~ str("badgeUri")

  private val badgesConverter =
    badgesParser map {
      case badgeName ~ badgeDescription ~ badgeStatus ~
        categoryId ~ categoryName ~ badgeUri =>
        Badge(
          name = badgeName,
          description = badgeDescription,
          status = badgeStatus,
          category = Category(
            id = categoryId,
            name = categoryName),
          uri = badgeUri)
    }

  private val userBadgesConverter =
    badgesParser ~ long("userId") ~ str("userName") map {
      case badgeName ~ badgeDescription ~ badgeStatus ~
        categoryId ~ categoryName ~ badgeUri ~
        userId ~ userName =>
        UserBadge(
          userId = userId,
          userName = userName,
          badge = Badge(
            name = badgeName,
            description = badgeDescription,
            status = badgeStatus,
            category = Category(
              id = categoryId,
              name = categoryName),
            uri = badgeUri))
    }

  private val ruleConverter =
    badgesParser ~ int("booksCount") map {
      case badgeName ~ badgeDescription ~ badgeStatus ~
        categoryId ~ categoryName ~ badgeUri ~ booksCount =>
        val category = Category(categoryId, categoryName)
        Rule(
          category = category,
          badge = Badge(
            name = badgeName,
            description = badgeDescription,
            status = badgeStatus,
            category = category,
            uri = badgeUri),
          booksCount = booksCount)
    }

  override def publicBadges: Future[List[Badge]] =
    Future.successful{
      DB.withConnection{implicit c =>
        SQL(PublicBadgesSQL).as(badgesConverter.*).toList
      }
    }

  override def badges(userId: Long): Future[List[UserBadge]] =
    Future.successful{
      DB.withConnection{implicit c =>
        SQL(UserBadgesSQL)
          .on("userId" -> userId)
          .as(userBadgesConverter.*).toList
      }
    }

  override def saveBadge(badgePerUser: UserBadge): Future[Unit] =
    Future.successful{
      DB.withConnection{implicit c =>
        SQL(AddUserBadgeSQL)
          .on("userId" -> badgePerUser.userId)
          .on("userName" -> badgePerUser.userName)
          .on("badgeName" -> badgePerUser.badge.name)
          .execute()
      }
    }

  override def bisac(bisacId: String): Future[Option[Bisac]] =
    Future.successful{
      DB.withConnection{implicit c =>
        SQL(BisacSQL)
          .on("bisacId" -> bisacId)
          .as((str("bisacId") ~ int("categoryId") ~ str("categoryName")).singleOpt)
          .map {
            case parsedBisacId ~ categoryId ~ categoryName => Bisac(
              id = parsedBisacId,
              Category(
                id = categoryId,
                name = categoryName))
          }
      }
    }

  override def rule(bisac: Bisac): Future[Option[Rule]] =
    Future.successful{
      DB.withConnection{implicit c =>
        SQL(RuleByCategoryIdSQL)
          .on("categoryId" -> bisac.category.id)
          .as(ruleConverter.singleOpt)
      }
    }
}
