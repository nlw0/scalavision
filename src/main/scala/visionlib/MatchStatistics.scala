package visionlib

object MatchStatistics extends VisionApp with TestKeypointExtractor with CoasterData {

  val nImages = 5

  val myImages = allImages.take(nImages).flatten.toVector

  val descriptors = myImages map { im => kpext.detectAndDescribe(im) }

  val matches = (for {mm <- 0 until nImages
                      nn <- 0 until nImages
                      if mm != nn} yield {
    val kda = descriptors(mm)
    val kdb = descriptors(nn)
    (mm, nn) -> matchKeypoints(kda, kdb)
  }).toMap

  for {mm <- 0 until nImages
       nn <- 0 until nImages
       if mm != nn} {

    val ima = myImages(mm)
    val imb = myImages(nn)
    val mkp = matches((mm, nn))

    val MatchingKeypoints(aa, bb, dd) = mkp

    val outImg = drawTracksBoth(ima, imb, mkp)
    val outImgTrans = drawTransformsBoth(ima, imb, mkp)

    def filename = { num: Int => f"/home/nlw/coisa-$num%02d.png" }

    def filenameTrans = { num: Int => f"/home/nlw/coisatrans-$num%02d.png" }

    val ii = mm * (nImages - 1) + nn

    saveToFile(filename(ii))(outImg)
    saveToFile(filenameTrans(ii))(concatenateImagesVertical(outImg, outImgTrans))
  }
}
