package services

import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.blinkbox.books.logging.DiagnosticExecutionContext
import com.blinkbox.books.messaging.ActorErrorHandler
import com.blinkbox.books.rabbitmq.RabbitMq
import com.blinkbox.books.rabbitmq.RabbitMqConfig
import com.blinkbox.books.rabbitmq.RabbitMqConfirmedPublisher
import com.blinkbox.books.rabbitmq.RabbitMqConfirmedPublisher.PublisherConfiguration
import com.blinkbox.books.rabbitmq.RabbitMqConsumer
import com.blinkbox.books.rabbitmq.RabbitMqConsumer.QueueConfiguration
import com.typesafe.config.Config
import scala.concurrent.duration._

class MessagingContext(actorSystem: ActorSystem, config: Config) {
  implicit val msgExecutionCtx = DiagnosticExecutionContext(actorSystem.dispatcher)
  implicit val apiTimeout = Timeout(config.getDuration("messageListener.actorTimeout", TimeUnit.SECONDS).seconds)

  private val rabbitmqConfig = RabbitMqConfig(config)
  private val consumerConnection = RabbitMq.reliableConnection(rabbitmqConfig)
  private val publisherConnection = RabbitMq.recoveredConnection(rabbitmqConfig)

  private val errorsPublisher = actorSystem.actorOf{
    val pubConfig = PublisherConfiguration(
      config.getConfig("messageListener.event.errors"))
    Props(new RabbitMqConfirmedPublisher(
      connection = publisherConnection,
      config = pubConfig))
  }
  private val consumer = actorSystem.actorOf{
    Props(new MessageHandler(
      errorHandler = new ActorErrorHandler(errorsPublisher),
      retryInterval = 10.seconds))
  }
  private val messageConsumer = actorSystem.actorOf{
    val queueConf = QueueConfiguration(
      config.getConfig("messageListener.event.input"))
    Props(new RabbitMqConsumer(
      channel = consumerConnection.createChannel(),
      queueConfig = queueConf,
      consumerTag = "test-tag",
      output = consumer))
  }

  messageConsumer ! RabbitMqConsumer.Init
}
