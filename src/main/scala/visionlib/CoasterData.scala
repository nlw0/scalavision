package visionlib

trait CoasterData extends UtilityFunctions {
  def allImages = resourcesFromDirectory("/coaster").toStream map { s => openResource(s) }

  def openResource(res: String, inputSize: Int = 400) = getFilenameFromResource(res) map
                                                        loadImage map
                                                        scaleImageHeight(inputSize)
}
