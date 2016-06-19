import akka.actor.{ActorSystem, Props}
import org.opencv.core.Core

import javafx.embed.swing.JFXPanel

//import javafx.embed.swing.JFXPanel

import scala.concurrent.duration._

import scala.language.postfixOps

object ParallelObjectTracker extends App {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  new JFXPanel(); // trick: create empty panel to initialize toolkit
  new Thread(new Runnable() {
    override def run(): Unit = {
      GUI.main(Array[String]())
    }
  }).start()

  val system = ActorSystem("AkkaVision")

  val viewActor = system.actorOf(Props[ViewActor].withDispatcher("javafx-dispatcher"), "javaFxActor")
  val spinnerActor = system.actorOf(Props[SpinnerActor], "spinnerActor")

  import system.dispatcher

  system.scheduler.schedule(0 seconds, 100 millis) {
    val t = System.nanoTime()
    for (x <- 0 until 100)
      spinnerActor ! SpinnerActorTick(t + x * 1000000)
  }
  viewActor ! ViewActorUpdate
}