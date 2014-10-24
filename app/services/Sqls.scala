package services

trait Sqls {
  val BadgesSQL =
    """
      SELECT
        b.name AS "badgeName",
        b.description AS "badgeDescription",
        b.status AS "badgeStatus",
        b.uri AS "badgeUri",
        c.id AS "categoryId",
        c.name AS "categoryName"
      FROM
        badges b, categories c
      WHERE
        b.category_id = c.id
    """

  val PublicBadgesSQL =
    s"$BadgesSQL AND b.status = 'public'"

  val UserBadgesSQL =
    """
      SELECT
        u.user_id AS "userId",
        u.user_name AS "userName",
        b.name AS "badgeName",
        b.description AS "badgeDescription",
        b.status AS "badgeStatus",
        b.uri AS "badgeUri",
        c.id AS "categoryId",
        c.name AS "categoryName"
      FROM
        userbadges u, badges b, categories c
      WHERE
        u.badge_name = b.name
        AND b.category_id = c.id
        AND u.user_id = {userId}
    """

  val BisacSQL =
    """
      SELECT
        b.id AS "bisacId",
        c.id AS "categoryId",
        c.name AS "categoryName"
      FROM
        bisacs b, categories c
      WHERE
        b.category_id = c.id
        AND b.id = {bisacId}
    """

  val RuleByCategoryIdSQL =
    """
      SELECT
        r.bookscount AS "booksCount",
        c.id AS "categoryId",
        c.name AS "categoryName",
        b.name AS "badgeName",
        b.description AS "badgeDescription",
        b.status AS "badgeStatus",
        b.uri AS "badgeUri"
      FROM
        badges b, rules r, categories c
      WHERE
        r.category_id = c.id
        AND r.badge_name = b.name
        AND r.category_id = {categoryId}
    """

  val AddUserBadgeSQL =
    """
      INSERT INTO userbadges
      VALUES({userId}, {userName}, {badgeName})
    """
}
