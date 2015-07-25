import me.trial.actors.Reader.GetRate
import me.trial.actors.{DataCoordinator, Reader}
import me.trial.model.CurrencyRate

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout

import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}


class ActorTest extends TestKit(ActorSystem("GetterSuite"))
                                      with FunSuiteLike
                                      with Matchers
                                      with BeforeAndAfterAll
                                      with ImplicitSender  {
  override def afterAll(): Unit = system.shutdown()

  test("Getter Actor - should return value putted by DataCoordinator service") {
    implicit val timeout = Timeout(2 seconds)
    system.actorOf(Props[DataCoordinator],"requester")

    val reader = system.actorOf(Props[Reader])
    reader ! GetRate("BYR")
    expectMsgType[CurrencyRate]
//    val db = Database.forConfig("h2storage")
//    println(currencyRateByCode("BYR").result.statements)
//    db.run(currencyRateByCode("BYR").result).map(_.foreach {
//      case CurrencyRate(code, name, rate, tstamp) =>
//        println("  " + code + "\t" + name + "\t" + rate + "\t" + tstamp)
//    })
//    db.close()
  }
}