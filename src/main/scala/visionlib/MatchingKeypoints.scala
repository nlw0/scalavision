package visionlib

import org.opencv.calib3d.Calib3d
import org.opencv.core.{DMatch, MatOfKeyPoint, MatOfPoint2f, Point}

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: Seq[DMatch]) {
  def homography = {
    val lpta = descriptorMatches map { aa => kpa.toArray.apply(aa.queryIdx).pt } // map transformPoint
    val lptb = descriptorMatches map { aa => kpb.toArray.apply(aa.trainIdx).pt } // map transformPoint
    val srcPoints = getMatOfPoints(lpta)
    val dstPoints = getMatOfPoints(lptb)

    Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8)
  }

  def transformPoint(pp: Point): Point = {
    new Point((pp.x - 400.0f) / 200.0f, (pp.y - 300.0f) / 200.0f)
  }

  def getMatOfPoints(listOfPoints: Seq[Point]) = {
    val out = new MatOfPoint2f()
    out.fromArray(listOfPoints: _*)
    out
  }

}
