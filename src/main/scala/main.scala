package runtime

import akka.stream._
import akka.stream.scaladsl._

import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

object Sink {
  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map { s => ByteString(s + "\n") }
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}

object Main extends App {

  println("Hello world")

  val source: Source[Int, NotUsed] = Source(1 to 100)

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  //source.runForeach(i => println(i))(materializer)

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] =
    factorials
      .zipWithIndex
      .map { case (value, index) => index + "! = " + value}
      .runWith(Sink.lineSink("factorial1.txt"))



    // factorials
    //   .map(num => ByteString(s"$num"))
    //   .runWith(Sink.linkSink("factorial.txt"))




}
