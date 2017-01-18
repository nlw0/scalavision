package visionlib

import breeze.linalg.DenseMatrix
import org.opencv.calib3d.Calib3d
import org.opencv.core._

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: Seq[DMatch]) {
  def lpta = descriptorMatches map { aa => kpa.toArray.apply(aa.queryIdx).pt } map transformPoint
  def lptb = descriptorMatches map { aa => kpb.toArray.apply(aa.trainIdx).pt } map transformPoint
  def srcPoints = getMatOfPoints(lpta)

  def dstPoints = getMatOfPoints(lptb)

  def homographyMat = {
    println(srcPoints.dump)
    println(dstPoints.dump)
    Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8.0)
  }

  def homographyOutliers = {
    val mask = new Mat()
    val aa = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 2.0, mask, 1000, 0.99)
    (mat2dToDMDouble(aa), mask)
  }

  def homography = mat2dToDMDouble(homographyMat)

  def mat2dToDMDouble(m: Mat) = {
    val hh = Array.fill[Double](m.rows * m.cols)(0.0)
    m.get(0, 0, hh)
    new DenseMatrix(m.rows, m.cols, hh)
  }

  def transformPoint(pp: Point): Point = {
    new Point((pp.x - 320.0f) / 150.0f, (pp.y - 240.0f) / 150.0f)
  }

  def getMatOfPoints(listOfPoints: Seq[Point]) = {
    val out = new MatOfPoint2f()
    out.fromArray(listOfPoints: _*)
    out
  }
}
