package visionlib

import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

trait UtilityFunctions {

  def cannyFilter = {
    val theFilter = new Mat(3, 3, CvType.CV_32F)
    val cannyCoefficients = Array(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
    theFilter.put(0, 0, cannyCoefficients)
    theFilter
  }

  def colorToGray(mat: Mat) =
    if (mat.channels == 1) mat else {
    val imgGray = new Mat()
    println("**" + mat.channels())
    Imgproc.cvtColor(mat, imgGray, Imgproc.COLOR_RGB2GRAY)
    imgGray
  }

  def grayToColor(mat: Mat) = {
    val imgColor = new Mat()
    Imgproc.cvtColor(mat, imgColor, Imgproc.COLOR_GRAY2RGB)
    imgColor
  }

  def applyFilter(image: Mat, filter: Mat) = {
    val outImg = new Mat(image.rows(), image.cols(), image.`type`())
    Imgproc.filter2D(image, outImg, -1, filter)
    outImg
  }

  def asFloat(input: Mat) = {
    val outputType = if (input.channels() == 3) CvType.CV_32FC3 else CvType.CV_32F
    asType(input, outputType)
  }

  def asType(input: Mat, matType: Int, alpha: Double = 1.0, beta: Double = 0.0) = {
    val output = new Mat(input.rows(), input.cols(), matType)
    input.convertTo(output, matType, alpha, beta)
    output
  }

  def writeFloat(outputFloat: Mat, outFilename: String): Unit = {
    def outputInt = asType(outputFloat, CvType.CV_8UC3, 0.5, 128)

    Imgcodecs.imwrite(outFilename, outputInt)
  }

  def scaleImageHeight(rows: Int = 400)(img: Mat) = {
    val out = new Mat(rows, img.cols() * rows / img.rows(), img.`type`())
    Imgproc.resize(img, out, out.size(), 0, 0, Imgproc.INTER_AREA)
    out
  }

  def fileNamesFromDirectory(directory:String) = {
    val filenamesStream = getClass.getResourceAsStream(directory + "/")
    val fileNames = scala.io.Source.fromInputStream(filenamesStream).getLines()
    fileNames map { fileName =>
      s"$directory/$fileName"
    }
  }


}

object UtilityFunctions extends UtilityFunctions