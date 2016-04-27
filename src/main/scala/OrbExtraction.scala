import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d._

object OrbExtraction extends App with UtilityFunctions {

  println(System.getProperty("java.library.path"))
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val imageFilename = getClass.getResource("/buska.jpg").getPath
  // val imageFilename = getClass.getResource("/left05.jpg").getPath
  val inputImage = Imgcodecs.imread(imageFilename, Imgcodecs.IMREAD_GRAYSCALE)

  val orb = FeatureDetector.create(FeatureDetector.ORB)

  val kp = new MatOfKeyPoint()
  val qq = orb.detect(inputImage, kp)

  val resultImg = new Mat
  Features2d.drawKeypoints(inputImage, kp, resultImg)

  Imgcodecs.imwrite("denom.png", resultImg)
}
