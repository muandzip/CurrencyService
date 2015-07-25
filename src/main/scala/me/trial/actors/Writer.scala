package me.trial.actors

import akka.actor.Actor
import me.trial.model.CurrencyRateTable._
import slick.driver.H2Driver.api._

class Writer extends Actor{
  val db = Database.forConfig("h2storage")

  override def preStart() =
    db.run {
      currencyRateQuery.schema.create
    }
  override def postStop() =
    db.close()

  override def receive: Receive = {
    case Fetcher.Payload(msg) =>
      db.run {
        currencyRateQuery ++= msg
      }
  }
}
