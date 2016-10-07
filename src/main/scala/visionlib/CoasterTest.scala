package visionlib


object CoasterTest extends VisionApp with TestKeypointExtractor {

  val INPUT_SIZE = 400

  for ((Seq(imga, imgb), nn) <- imagePairs.zipWithIndex) {
    val outputImage = findAndDrawTracksBoth(imga, imgb)

    def filename = { num: Int => f"/home/nlw/coisa-$num%02d.png" }

    saveToFile(filename(nn))(outputImage)
  }

  def imagePairs = resourcesFromDirectory("/coaster") map openResource sliding 2

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)
}
