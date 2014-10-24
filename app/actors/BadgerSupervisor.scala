package actors

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{OneForOneStrategy, Actor}
import play.api.Logger
import scala.concurrent.duration._

class BadgerSupervisor extends Actor{
  override val supervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10.seconds, loggingEnabled = true){
      case _: Exception => Resume
    }
  }

  override def receive: Receive = {
    case msg => Logger.warn(s"Got some nasty message [$msg]")
  }
}
