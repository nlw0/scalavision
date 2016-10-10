package visionlib

import org.opencv.core.Mat


object Tracker extends TestKeypointExtractor {
  var lastImage: Option[Mat] = None

  def memtrack(image: Mat) = {
    val out = lastImage map {li =>

      //TestKeypointExtractor.findAndDrawTracks(li, image)

      val imfta = extractFeatures(li)
      val imftb = extractFeatures(image)

      val mkp = matchKeypoints(imfta.kps, imftb.kps)

      val im = drawTracks(imftb.image, mkp)
      drawTranslation(im, mkp)

  }





    lastImage = Some(image)
    out
  }

  def track(imfts: ImageAndDescriptors*): Some[Mat] = track(imfts(0), imfts(1))

  def track(imfta: ImageAndDescriptors, imftb: ImageAndDescriptors): Some[Mat] = {
    val mkp = matchKeypoints(imfta.kps, imftb.kps)

    val im = drawTracks(imftb.image, mkp)
    Some(drawTranslation(im, mkp))
  }

}
