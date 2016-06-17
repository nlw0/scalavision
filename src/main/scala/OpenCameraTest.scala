import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc
import org.opencv.videoio.{VideoCapture, Videoio}

import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.image.{ImageView, PixelFormat, WritableImage}
import scalafx.scene.{Group, Scene}


object OpenCameraTest extends JFXApp {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val matOrig = new MatOfByte()

  val capture = new VideoCapture(0)

  val w: Int = capture.get(Videoio.CAP_PROP_FRAME_WIDTH).toInt
  val h: Int = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT).toInt

  val wi = new WritableImage(w, h)
  val iv = new ImageView(wi)
  val rootPane = new Group
  rootPane.children = List(iv)
  stage = new PrimaryStage {
    title = "scalavision"
    scene = new Scene(640, 480) {
      root = rootPane

      var oi = 0L

      val timer = AnimationTimer { t =>
        println(t - oi)
        if (capture.isOpened()) {

          capture.read(matOrig)
          if (!matOrig.empty()) {
            val matrgb = new Mat()
            Imgproc.cvtColor(matOrig, matrgb, Imgproc.COLOR_BGR2RGB)
            Imgproc.circle(matrgb, new Point(10, 10), 3, new Scalar(255,0,0), -1)
            val was = new Array[Byte](matOrig.total().toInt * matOrig.channels())
            matrgb.get(0, 0, was)
            pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w * 3)
          }
        }
        oi = t
      }

      timer.start
    }
  }

  val pw = wi.pixelWriter

  val was = Array.fill[Byte](640 * 480 * 3)(0.toByte)
  pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w)
}
