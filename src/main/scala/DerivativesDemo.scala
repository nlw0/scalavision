import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

object DerivativesDemo extends App {

  def cannyFilter = {
    val theFilter = new Mat(3, 3, CvType.CV_32F)
    val cannyCoefficients = Array(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
    theFilter.put(0, 0, cannyCoefficients)
    theFilter
  }

  def applyFilter(image: Mat, filter: Mat) = {
    val outImg = new Mat(image.rows(), image.cols(), image.`type`())
    Imgproc.filter2D(image, outImg, -1, filter)
    outImg
  }

  def asType(input: Mat, matType: Int, alpha: Double = 1.0, beta: Double = 0.0) = {
    val output = new Mat(input.rows(), input.cols(), matType)
    input.convertTo(output, matType, alpha, beta)
    output
  }

  def asFloat(input: Mat) = {
    val outputType = if (input.channels() == 3) CvType.CV_32FC3 else CvType.CV_32F
    asType(input, outputType)
  }

  var libopencv_java = System.getProperty("opencvlib")
  System.load(libopencv_java)

  val imageFilename = getClass.getResource("/buska.jpg").getPath
  val imageInt = Imgcodecs.imread(imageFilename)
  val imageFloat = asType(imageInt, CvType.CV_32FC3)

  val outputFloat = applyFilter(imageFloat, cannyFilter)

  def outputInt = asType(outputFloat, CvType.CV_8UC3, 0.5, 128)

  val outFilename = "faceDetection.png"
  System.out.println(String.format("Writing %s", outFilename))
  Imgcodecs.imwrite(outFilename, outputInt)
}
