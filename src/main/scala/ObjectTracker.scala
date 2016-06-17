import DetectFaceDemo._
import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.{VideoCapture, Videoio}

import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.image.{ImageView, PixelFormat, WritableImage}
import scalafx.scene.{Group, Scene}


object ObjectTracker extends JFXApp {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val matOrig = Imgcodecs.imread(getClass.getResource("/mahakala.jpg").getPath)

  var w = matOrig.cols()
  var h = matOrig.rows()

  val wi = new WritableImage(w, h)
  val iv = new ImageView(wi)
  val rootPane = new Group
  rootPane.children = List(iv)

  stage = new PrimaryStage {
    title = "scalavision"
    scene = new Scene(640, 480) {
      root = rootPane

      val timer = AnimationTimer { t =>
        val matrgb = new Mat()

        Imgproc.cvtColor(matOrig, matrgb, Imgproc.COLOR_BGR2RGB)

        val x = 100 + 10 * Math.sin(10*2*Math.PI*t*1e-9)
        val y = 100 + 10 * Math.cos(10*2*Math.PI*t*1e-9)
        Imgproc.circle(matrgb, new Point(x, y), 3, new Scalar(255, 0, 0), -1)
        val was = new Array[Byte](matOrig.total().toInt * matOrig.channels())
        matrgb.get(0, 0, was)
        pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w * 3)
      }

      timer.start
    }
  }

  val pw = wi.pixelWriter
  val was = Array.fill[Byte](640 * 480 * 3)(0.toByte)
  pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w)
}
