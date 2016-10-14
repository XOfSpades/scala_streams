package runtime

import akka.stream._
import akka.stream.scaladsl._

import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

object Main extends App {

  println("Hello world")

  val source: Source[Int, NotUsed] = Source(1 to 100)

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  //source.runForeach(i => println(i))(materializer)

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] =
    factorials
      .map(num => ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("factorials.txt")))



}
