package visionlib

import org.opencv.core.{Mat, Scalar}
import org.opencv.imgproc.Imgproc


object Tracker extends TestKeypointExtractor {
  var lastImage: Option[Mat] = None

  def memtrack(image: Mat) = {
    val out = lastImage map {li =>

      //TestKeypointExtractor.findAndDrawTracks(li, image)

      val imfta = extractFeatures(li)
      val imftb = extractFeatures(image)

      val mkp = matchKeypoints(imfta.kps, imftb.kps)

      val (ax, ay) = estimateTransform(mkp)
      val im = drawTracks(imftb.image, mkp)
      drawTransform(im, mkp)

  }





    lastImage = Some(image)
    out
  }

  def track(imfts: ImageAndDescriptors*): Some[Mat] = track(imfts(0), imfts(1))

  def track(imfta: ImageAndDescriptors, imftb: ImageAndDescriptors): Some[Mat] = {
    val mkp = matchKeypoints(imfta.kps, imftb.kps)

    val (ax, ay) = estimateTransform(mkp)
    val im = drawTracks(imftb.image, mkp)
    Some(drawTransform(im, mkp))
  }

}
