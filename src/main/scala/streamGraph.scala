package myStreams

import akka.stream._
import akka.stream.scaladsl._
import akka.stream.Graph

import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._

object StreamGraph {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  // See:
  // http://doc.akka.io/docs/akka/current/scala/stream/stream-composition.html

  import GraphDSL.Implicits._

  def runStream {
    val flowPartial = flow()

    val sourcePartial = source()

    val sinkPartial = sink()

    val graph =
      Source.fromGraph(sourcePartial)
            .via(flowPartial)
            .to(Sink.fromGraph(sinkPartial))
    graph.run
  }

  def flow(): Graph[akka.stream.FlowShape[Int, Int],akka.NotUsed] = {
    GraphDSL.create() { implicit builder =>
      val evenSelector = builder.add(Flow[Int].filter(_ % 2 == 0))
      val oddSelector = builder.add(Flow[Int].filter(_ % 2 == 1))
      val square = builder.add(Flow[Int].map { i => i * i })
      val cubics = builder.add(Flow[Int].map{ i => i * i * i })
      val negative = builder.add(Flow[Int].map{ i => -1 * i })

      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[Int](2))

      bcast.out(0) ~> evenSelector ~> cubics ~> merge.in(0)
      bcast.out(1) ~> oddSelector ~> square ~> negative ~> merge.in(1)

      FlowShape(bcast.in,  merge.out)
    }.named("FlowPartial")
  }

  def source(): Graph[akka.stream.SourceShape[Int],akka.NotUsed] = {
    GraphDSL.create() { implicit builder =>
      val source = builder.add(Source(1 to 10))
      SourceShape(source.out)
    }.named("SourcePartial")
  }

  def sink(): Graph[akka.stream.SinkShape[Int],akka.NotUsed] = {
    GraphDSL.create() { implicit builder =>
      val toString = builder.add(Flow[Int].map{ i => i.toString + "\n" })
      val sink = builder.add(Sink.foreach { i: String => println(i) })

      toString.out ~> sink

      SinkShape(toString.in)
    }.named("SinkPartial")
  }
}
