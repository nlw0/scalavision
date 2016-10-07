package visionlib

import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs


object CoasterTest extends App with TestKeypointExtractor {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  for ((Seq(imga, imgb), nn) <- imagePairs.zipWithIndex) {
    val qq = findAndDrawTracksBoth(imga, imgb)
    Imgcodecs.imwrite(f"/home/nlw/coisa-$nn%02d.png", qq)
  }

  private val imagePairs = fileNamesFromDirectory("/coaster") map openResource sliding 2

  def openResource = (getClass.getResource(_: String).getPath) andThen
                     (Imgcodecs.imread(_: String)) andThen
                     scaleImageHeight(400)
}

