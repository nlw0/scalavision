import java.io.File

import org.opencv.core.{Core, Mat}
import org.opencv.imgcodecs.Imgcodecs

object PrepareDataset extends App {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val dir = new File("/home/nlw/DATA/coaster")

  def images = dir.listFiles.toStream map { file => Imgcodecs.imread(file.toString) }

  for (((imga, imgb), n) <- (images zip images.tail).zipWithIndex) {
    println(n)
    val out = TestKeypointExtractor.findAndDrawCorrespondences(imga, imgb)
    Imgcodecs.imwrite(f"$n%02d.jpg", out)
  }

}
