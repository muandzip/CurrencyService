package me.trial.actors

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.pattern.pipe
import me.trial.model.CurrencyRate
import me.trial.utils.MyJsonProtocol._
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

class Fetcher(url: String, timeout: Duration) extends Actor{
  import Fetcher._
  import context.dispatcher

  context.setReceiveTimeout(timeout)

  Future {
    Source.fromURL(url).mkString
  }
//    Promise[String]().future    //Future.never
    .map(_.parseJson.convertTo[List[CurrencyRate]])
    .map(Payload)
    .pipeTo(self)

  override def receive = {
    case msg: Payload =>
      context.parent ! msg
      context.stop(self)
    case ReceiveTimeout =>
      throw new TimeoutException
    case _: Status.Failure =>
      throw new Exception
  }
}

object Fetcher {
  case class Payload(content: List[CurrencyRate])
  def props(url: String, timeout: Duration): Props = Props(new Fetcher(url, timeout))
}