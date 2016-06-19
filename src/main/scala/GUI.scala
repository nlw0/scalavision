import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.image.{ImageView, PixelFormat, WritableImage}
import scalafx.scene.{Group, Scene}

object GUI extends JFXApp {

  def drawImage(matrgb: Mat): Unit = {
    val was = new Array[Byte](matOrig.total().toInt * matOrig.channels())
    matrgb.get(0, 0, was)
    pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w * 3)
  }

  //  var w = matOrig.cols()
  //  var h = matOrig.rows()
  var w = 500
  var h = 500

  val matOrig = new Mat()
  val matx = Imgcodecs.imread(getClass.getResource("/grolsch.jpg").getPath)
  Imgproc.resize(matx, matOrig, new Size(w, h))

  val wi = new WritableImage(w, h)
  val iv = new ImageView(wi)
  val rootPane = new Group
  rootPane.children = List(iv)

  val pw = wi.pixelWriter
  val was = Array.fill[Byte](w * h * 3)(0.toByte)
  pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w)

  var fps = new FrequencyMeter()

  stage = new PrimaryStage {
    title = "scalavision"
    scene = new Scene(w, h) {
      root = rootPane

      val timer = AnimationTimer { t =>
//        println(f"[GUI] current FPS: $fps")
        fps.update(t)
      }

      timer.start
    }
  }
}
