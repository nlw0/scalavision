package visionlib

import org.opencv.core.Mat

object Tracker {
  var lastImage: Option[Mat] = None

  def memtrack(image: Mat) = {
    val out = lastImage map (li => TestKeypointExtractor.findAndDrawTracks(li, image))

    lastImage = Some(image)
    out
  }

  def track(imfta: ImageAndDescriptors, imftb: ImageAndDescriptors) = {
    val mkp = TestKeypointExtractor.matchKeypoints(imfta.kps, imftb.kps)
    Some(TestKeypointExtractor.drawTracks(imftb.image, mkp))
  }

}
