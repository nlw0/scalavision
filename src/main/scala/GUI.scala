import org.opencv.imgcodecs.Imgcodecs

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.image.{ImageView, PixelFormat, WritableImage}
import scalafx.scene.{Group, Scene}

object GUI extends JFXApp {
  val matOrig = Imgcodecs.imread(getClass.getResource("/mahakala.jpg").getPath)

  var w = matOrig.cols()
  var h = matOrig.rows()

  val wi = new WritableImage(w, h)
  val iv = new ImageView(wi)
  val rootPane = new Group
  rootPane.children = List(iv)

  val pw = wi.pixelWriter
  val was = Array.fill[Byte](640 * 480 * 3)(0.toByte)
  pw.setPixels(0, 0, w, h, PixelFormat.getByteRgbInstance, was, 0, w)

  stage = new PrimaryStage {
    title = "scalavision"
    scene = new Scene(w, h) {
      root = rootPane
    }
  }
}
