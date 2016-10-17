package myStreams

import akka.stream._
import akka.stream.scaladsl.{ Source, Sink }
import akka.actor.{ ActorSystem }
import akka.testkit.{ TestKit, TestProbe }

import scala.concurrent.duration._
import scala.concurrent._

import org.scalatest.{ WordSpecLike, Matchers, BeforeAndAfterAll }

class StreamGraphSpec extends TestKit(ActorSystem("KidSpec"))
  with WordSpecLike with Matchers {

  implicit val materializer = ActorMaterializer()

  "A Flow" must {

    "stream data correctly when input has only odd values" in {
      val flow = StreamGraph.flow()

      val testSource = Source(1 to 10 by 2)

      val future =
        testSource.via(flow)
                  .runWith(Sink.fold(Seq.empty[Int])(_ :+ _))
      val result = Await.result(future, 3.seconds)

      assert(result == Seq(-1, -9, -25, -49, -81))

    }

    "stream data correctly when input has only even values" in {
      val flow = StreamGraph.flow()

      val testSource = Source(2 to 10 by 2)

      val future =
        testSource.via(flow)
                  .runWith(Sink.fold(Seq.empty[Int])(_ :+ _))
      val result = Await.result(future, 3.seconds)

      assert(result == Seq(8, 64, 216, 512, 1000))

    }

    "stream data correctly when input has odd and even values" in {
      val flow = StreamGraph.flow()

      val testSource = Source(1 to 10)

      val future =
        testSource.via(flow)
                  .runWith(Sink.fold(Seq.empty[Int])(_ :+ _))
      val result = Await.result(future, 3.seconds)

      assert(result == Seq(-1, 8, -9, 64, -25, 216, -49, 512, -81, 1000))

    }
  }
}
