import javafx.application.Application
import javafx.embed.swing.JFXPanel

import ObjectTracker._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.{ActorSystem, Props}
import org.opencv.core.{Core, Mat, Point, Scalar}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Group, Scene}
import scalafx.scene.image.{ImageView, PixelFormat, WritableImage}

class JavaFxActor extends Actor {

  def receive = {
    case t: Int =>
      val matrgb = new Mat()

      Imgproc.cvtColor(GUI.matOrig, matrgb, Imgproc.COLOR_BGR2RGB)

      val x = 100.0 + 20.0 * Math.sin(10 * 2 * Math.PI * t * 1e-9)
      val y = 100.0 + 20.0 * Math.cos(10 * 2 * Math.PI * t * 1e-9)
      Imgproc.circle(matrgb, new Point(x.toInt, y.toInt), 3, new Scalar(255, 0, 0), -1)
      val was = new Array[Byte](GUI.matOrig.total().toInt * GUI.matOrig.channels())
      matrgb.get(0, 0, was)
      GUI.pw.setPixels(0, 0, GUI.w, GUI.h, PixelFormat.getByteRgbInstance, was, 0, GUI.w * 3)

      println(s"hello back at you $x $y")

    case _ => println("huh?")
  }
}


object MyCode extends App {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  new JFXPanel(); // trick: create empty panel to initialize toolkit
  new Thread(new Runnable() {
    override def run(): Unit = {
      GUI.main(Array[String]())
    }
  }).start()

  val system = ActorSystem("AkkaVision")
  //  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
  val helloActor = system.actorOf(Props[JavaFxActor].withDispatcher("javafx-dispatcher"), "javaFxActor")

  for (i <- 1 to 10000) {
    helloActor ! i * 1000000000
    Thread.sleep(10)
  }
}






