package runtime

// import akka.stream._
// import akka.stream.scaladsl._

// import akka.{ NotUsed, Done }
// import akka.actor.ActorSystem
// import akka.util.ByteString

import myStreams.{ StreamGraph, Factorial}

import scala.concurrent._
import scala.concurrent.duration._

import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global

// object SinkGenerator {
//   def lineSink(filename: String): Sink[Int, Future[IOResult]] =
//     Flow[Int]
//       .map { s => ByteString(s + "\n") }
//       .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
// }

object Main extends App {

  println("Hello world")

  async {
    StreamGraph.runStream
    Factorial.runStream
  }

  Thread.sleep(2000)
}
