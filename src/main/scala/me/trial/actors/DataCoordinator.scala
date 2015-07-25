package me.trial.actors

import java.util.concurrent.TimeoutException

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import me.trial.utils.Config

import scala.concurrent.duration._
import scala.language.postfixOps

class DataCoordinator extends Actor with ActorLogging with Config{
  import DataCoordinator._
  import context.dispatcher

  context.system.scheduler.schedule(frequencyMillis, frequencyMillis, self, Tick)

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = retriesCount) {
    case _: TimeoutException =>
      log.error("TimeoutException handled"); Restart
    case _: Exception =>
      log.error("Unrecognized error"); Stop
  }
  val persistAgent = context.actorOf(Props[Writer])

  override def receive: Receive = {
    case Tick  =>
      if (context.child("FetcherActor").isEmpty)
        context.actorOf(Fetcher.props(serviceUrl, timeoutMillis), "FetcherActor")
    case msg @ Fetcher.Payload(_) =>
      log.info(s"Update received")
      persistAgent ! msg
  }
}
object DataCoordinator extends Config {
  object Tick
  val serviceUrl = dataProviderConfig.getString("url")
  val timeoutMillis = dataProviderConfig.getInt("timeout") millis
  val frequencyMillis = dataProviderConfig.getInt("frequency") millis
  val retriesCount = dataProviderConfig.getInt("retries")
}