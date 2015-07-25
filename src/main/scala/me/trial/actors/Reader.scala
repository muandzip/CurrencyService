package me.trial.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import me.trial.model.CurrencyRate
import me.trial.model.CurrencyRateTable._
import slick.driver.H2Driver.api._

class Reader extends Actor with ActorLogging {
  import Reader._
  import context.dispatcher
  val db = Database.forConfig("h2storage")

  override def postStop() =
    db.close()

  override def receive: Receive = {
    case GetRate(_code) =>
      log.info("read command received")
      db.run {
        currencyRateByCode(_code).result.head
      } pipeTo sender()
  }
}
object Reader {
  case class GetRate(code: String)
}