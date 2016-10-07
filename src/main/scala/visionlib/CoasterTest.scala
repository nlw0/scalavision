package visionlib

import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs


object CoasterTest extends App with TestKeypointExtractor {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val INPUT_SIZE = 400

  for ((Seq(imga, imgb), nn) <- imagePairs.zipWithIndex) {
    val outputImage = findAndDrawTracksBoth(imga, imgb)

    def filename = { num: Int => f"/home/nlw/coisa-$num%02d.png" }

    saveToFile(filename(nn))(outputImage)
  }

  def imagePairs = fileNamesFromDirectory("/coaster") map openResource sliding 2

  def openResource = (getClass.getResource(_: String).getPath) andThen
                     (Imgcodecs.imread(_: String)) andThen
                     scaleImageHeight(INPUT_SIZE)

}

