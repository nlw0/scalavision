import akka.actor.{Actor, ActorRef}
import org.opencv.core.{Mat, Point, Scalar, Size}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import scalafx.scene.image.PixelFormat

case class SpinnerActorTick(t: Long)

case class SpinnerActorQuery(dest: ActorRef)

class SpinnerActor extends Actor {

  var point = new Point(0, 0)
  val matrgb = new Mat()
  val matOrig = new Mat()
  val matx = Imgcodecs.imread(getClass.getResource("/grolsch.jpg").getPath)
  Imgproc.resize(matx, matOrig, new Size(500, 500))
  Imgproc.cvtColor(matOrig, matrgb, Imgproc.COLOR_BGR2RGB)

  val a = 0.3
  val delta = 0.5
  val b = 0.5

  var spinnerRefreshRate = new FrequencyMeter()

  def receive = {
    case SpinnerActorTick(t) =>
      spinnerRefreshRate.tick()
      val x = 250.0 + 200.0 * Math.cos(2 * Math.PI * (t * 1e-9 * a + delta))
      val y = 250.0 + 200.0 * Math.sin(2 * Math.PI * (t * 1e-9 * b))
      point = new Point(x.toInt, y.toInt)
      Imgproc.circle(matrgb, point, 3, new Scalar(255, 0, 0), -1)

    case SpinnerActorQuery(dest) =>
      println(s"[domain] current rate: $spinnerRefreshRate")
      dest ! ViewActorDrawImage(matrgb)


    case _ => println("duh")
  }
}
