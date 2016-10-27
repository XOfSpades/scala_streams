package runtime

import akka.actor.ActorSystem

import akka.stream.OverflowStrategy.fail
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.{Sink, Flow}
import akka.stream.ActorMaterializer

case class Weather(zip : String, temp : Double, raining : Boolean)

object Weather {
  def run = {
    implicit val system = ActorSystem("DimplexActorSystem")
    implicit val materializer = ActorMaterializer()

    val weatherSource = Source.actorRef[Weather](Int.MaxValue, fail)

    val sunnySource = weatherSource.filter(!_.raining)

    val ref = Flow[Weather]
      .to(Sink.foreach { w :Weather => println(w) })
      .runWith(sunnySource)

    ref ! Weather("02139", 15.0, true)
    ref ! Weather("51503", 25.0, false)
    ref ! Weather("24365", 22.0, false)
    ref ! Weather("65432", 23.0, true)
  }
}

