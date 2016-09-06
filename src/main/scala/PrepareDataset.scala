import java.io.File

import org.opencv.core.{Core, Mat}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

object PrepareDataset extends App {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val dir = new File("/home/nlw/DATA/coaster")

  def images: Stream[Mat] = dir.listFiles.toStream.take(9) map
                            (_.toString) map
                            Imgcodecs.imread map
                            UtilityFunctions.scaleImageHeight(rows = 400)

  for (((imga, imgb), n) <- (images zip images.tail).zipWithIndex) {
    println(n)
    val out = TestKeypointExtractor.findAndDrawTracks(imga, imgb)
    Imgcodecs.imwrite(f"out/$n%02d.jpg", out)
  }

}
