import akka.testkit.TestActorRef
import me.trial.CurrencyService
import org.specs2.mutable.Specification
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

class RestApiSpec extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system
  val actorRef = TestActorRef[CurrencyService]
  val actor = actorRef.underlyingActor

  "The service" should {
    "return correct rate" in {
      Get("/rate/BYR") ~> actor.route ~> check {
        responseAs[String] must contain("Belarusian Ruble")
      }
    }
    "not handle unsupported methods" in {
      Put("/rate/BYR") ~> actor.route ~> check {
        handled should beFalse
      }
    }
  }
}
