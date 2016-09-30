package visionlib

import org.opencv.core.{Mat, Scalar}
import org.opencv.imgproc.Imgproc


object Tracker {
  var lastImage: Option[Mat] = None

  def memtrack(image: Mat) = {
    val out = lastImage map {li =>

      //TestKeypointExtractor.findAndDrawTracks(li, image)

      val imfta = TestKeypointExtractor.extractFeatures(li)
      val imftb = TestKeypointExtractor.extractFeatures(image)

      val mkp = TestKeypointExtractor.matchKeypoints(imfta.kps, imftb.kps)

      val (ax, ay) = TestKeypointExtractor.estimateTransform(mkp)
      val im = TestKeypointExtractor.drawTracks(imftb.image, mkp)
      TestKeypointExtractor.drawTransform(im, mkp)

  }





    lastImage = Some(image)
    out
  }

  def track(imfts: ImageAndDescriptors*): Some[Mat] = track(imfts(0), imfts(1))

  def track(imfta: ImageAndDescriptors, imftb: ImageAndDescriptors): Some[Mat] = {
    val mkp = TestKeypointExtractor.matchKeypoints(imfta.kps, imftb.kps)

    val (ax, ay) = TestKeypointExtractor.estimateTransform(mkp)
    val im = TestKeypointExtractor.drawTracks(imftb.image, mkp)
    Some(TestKeypointExtractor.drawTransform(im, mkp))
  }

}
