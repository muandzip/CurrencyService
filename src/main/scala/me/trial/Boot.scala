package me.trial

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import me.trial.utils.Config
import spray.can.Http

object Boot extends App with Config {
  def system: ActorSystem = ActorSystem("currency-service")
  val rootService = system.actorOf(Props[CurrencyService])

  private val httpAddress = serviceConfig.getString("url")
  private val httpPort = serviceConfig.getInt("port")

  IO(Http)(system) ! Http.Bind(rootService, httpAddress, httpPort)
}