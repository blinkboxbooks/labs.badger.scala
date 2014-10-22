package models

import org.joda.time.DateTime

case class BadgeLevel(value: String)
case class BadgeCategory(code: String, label: String)
case class BadgeStatus(value: String)
case class Badge(name: String, label: String, level: BadgeLevel, category: BadgeCategory, status: BadgeStatus)
case class User(id: String, name: String)
case class BadgePerUser(badge: Badge, user: User)
case class UserData(earnedBadges: List[Badge], canEarn: List[Badge])
case class Event(eventId: String, userId: Long, name: String, occurredAt: DateTime)
