package runtime

import akka.stream._
import akka.stream.scaladsl._

import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

object SinkGenerator {
  def lineSink(filename: String): Sink[Int, Future[IOResult]] =
    Flow[Int]
      .map { s => ByteString(s + "\n") }
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}

object Main extends App {

  println("Hello world")

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source(1 to 10)
    val out = SinkGenerator.lineSink("factorial1.txt") //Sink.ignore

    val bcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    val f1 = Flow[Int].filter(_ % 2 == 0)
    val f2 = Flow[Int].filter(_ % 2 == 1)
    val f3 = Flow[Int].map { value => value * value * value }
    val f4 = Flow[Int].map { value => -1 * value * value }

    in ~> bcast.in
    bcast.out(0) ~> f1 ~> f3 ~> merge ~> out
    bcast.out(1) ~> f2 ~> f4 ~> merge
    ClosedShape
  })
  g.run()
}
