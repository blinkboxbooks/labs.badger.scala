package models

import org.joda.time.DateTime

case class Category(id: Int, name: String)
case class Badge(name: String, description: String, status: String, category: Category, uri: String)
case class Bisac(id: String, category: Category)
case class Rule(category: Category, badge: Badge, booksCount: Int)
case class UserBadge(userId: Long, userName: String, badge: Badge)
case class Event(eventId: String, userId: Long, name: String, occurredAt: DateTime)
case class DisplayableBadge(name: String, description: String, owned: Boolean, badgeUri: Option[String])
case class UserData(name: String, badges: List[DisplayableBadge])
case class BisacsRead(bisac: Bisac, count: Int)
