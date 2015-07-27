import me.trial.actors.Reader.GetRate
import me.trial.actors.{DataCoordinator, Reader}
import me.trial.model.CurrencyRate

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.{Status, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}


class ActorTest extends TestKit(ActorSystem("GetterSuite"))
                                      with FunSuiteLike
                                      with Matchers
                                      with BeforeAndAfterAll
                                      with ImplicitSender  {

  override def afterAll(): Unit = system.shutdown()

  test("Getter Actor - should return valid XRate after some garbage due cold start.") {
    system.actorOf(Props[DataCoordinator],"requester")
    val reader = system.actorOf(Props[Reader])
    reader ! GetRate("BYR")
    within(3 seconds) {
      ignoreMsg {
        case msg: Status.Failure =>
          reader ! GetRate("BYR")
          true
      }
      expectMsgType[CurrencyRate]
    }
  }
}