package visionlib

import breeze.linalg.DenseMatrix
import org.opencv.calib3d.Calib3d
import org.opencv.core._

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: Seq[DMatch]) {
  def homographyMat = {
    val lpta = descriptorMatches map { aa => kpa.toArray.apply(aa.queryIdx).pt }
    // map transformPoint
    val lptb = descriptorMatches map { aa => kpb.toArray.apply(aa.trainIdx).pt }
    // map transformPoint
    val srcPoints = getMatOfPoints(lpta)
    val dstPoints = getMatOfPoints(lptb)

    Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8)
  }

  def homography = mat2dToDMDouble(homographyMat)

  def mat2dToDMDouble(m: Mat) = {
    val hh = Array.fill[Double](m.rows * m.cols)(0.0)
    m.get(0, 0, hh)
    new DenseMatrix(m.rows, m.cols, hh)
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
