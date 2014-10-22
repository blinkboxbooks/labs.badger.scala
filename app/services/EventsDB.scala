package services

import models.Event

import scala.concurrent.Future

trait EventsDB {
  def events(userId: Long): Future[List[Event]]
}
