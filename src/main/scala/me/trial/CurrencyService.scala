package me.trial

import akka.actor.{Props, Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import me.trial.actors.{DataCoordinator, Reader}
import me.trial.actors.Reader._
import me.trial.model.CurrencyRate
import spray.routing.HttpService
import spray.http.MediaTypes._
import spray.json._
import scala.concurrent.duration._
import scala.language.postfixOps

class CurrencyService extends Actor with HttpService {
  import me.trial.utils.MyJsonProtocol._
  implicit def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout(2 seconds)

  val dataProviderActor = context.actorOf(Props[DataCoordinator])
  val readerActor = context.actorOf(Props[Reader])

  val route =
    path("rate" / Segment) {
      currencyISOCode =>
        get {
          respondWithMediaType(`application/json`)
          complete {
            (readerActor ? GetRate(currencyISOCode)).mapTo[CurrencyRate].map(_.toJson.prettyPrint)
          }
        }
    }

  def receive: Receive =
    runRoute(route)
}