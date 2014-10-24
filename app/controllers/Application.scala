package controllers

import conf.Global
import play.api.mvc._
import play.api.libs.json._
import services.JsonConverters
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller with JsonConverters{
  private val db = Global.badgesDb
  private val users = Global.users

  def status = Action.async {
    Future.successful(Ok(Json.obj("status" -> "OK")))
  }

  def badges = Action.async {
    db.publicBadges.map(badge => Ok(Json.toJson(badge)))
  }

  def badgesForUser(userid: Long) = Action.async {
    users.userStatus(userid).map(userData => Ok(Json.toJson(userData)))
  }
}
