package services

import models.UserData

import scala.concurrent.Future

trait UserService {
  def userStatus(userId: Long): Future[List[UserData]]
}
