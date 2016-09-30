package visionlib

import java.util

import scala.collection.JavaConversions._
import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


object TestKeypointExtractor extends UtilityFunctions {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val kpext = new KeypointExtractor(FeatureDetector.PYRAMID_AKAZE, DescriptorExtractor.AKAZE)

  def openImage(fileName: String): Mat = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_GRAYSCALE)

  def concatenateImages(matA: Mat, matB: Mat) = {
    val m = new Mat(matA.rows(), matA.cols() + matB.cols(), matA.`type`())
    val cols = matA.cols()
    matA.copyTo(m.colRange(0, cols))
    matB.copyTo(m.colRange(cols, cols * 2))
    m
  }

  def extractFeatures(img: Mat) = ImageAndDescriptors(img, kpext.detectAndDescribe(img))

  def findKeypointMatches(ima: Mat, imb: Mat): MatchingKeypoints = {
    val kda = kpext.detectAndDescribe(ima)
    val kdb = kpext.detectAndDescribe(imb)

    matchKeypoints(kda, kdb)
  }

  def matchKeypoints(kda: ExtractedKeypoints, kdb: ExtractedKeypoints) = {

    val MAGIC_LOWE_THRESHOLD = 0.8

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2)
    val descriptorMatches = new util.ArrayList[MatOfDMatch]()
    matcher.knnMatch(kda.desc, kdb.desc, descriptorMatches, 2)

    val xx = for {Array(ma, mb) <- descriptorMatches map (_.toArray)
                  if ma.distance < MAGIC_LOWE_THRESHOLD * mb.distance} yield ma

    MatchingKeypoints(kda.kp, kdb.kp, xx)
  }

  def drawTracksBoth(ima: Mat, imb: Mat, mkp: MatchingKeypoints): Mat = {
    val imgATrack = new Mat()
    ima.copyTo(imgATrack)
    val imgBTrack = new Mat()
    imb.copyTo(imgBTrack)

    for (aa <- mkp.descriptorMatches) {
      val pa = mkp.kpa.toArray.apply(aa.queryIdx).pt
      val pb = mkp.kpb.toArray.apply(aa.trainIdx).pt
      Imgproc.line(imgATrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.line(imgBTrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.circle(imgATrack, pb, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgBTrack, pa, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgATrack, pa, 4, new Scalar(0, 0, 255))
      Imgproc.circle(imgBTrack, pb, 4, new Scalar(0, 0, 255))
    }

    concatenateImages(imgATrack, imgBTrack)
  }

  def drawTracks(image: Mat, mkp: MatchingKeypoints): Mat = {
    val imgBTrack = new Mat()
    image.copyTo(imgBTrack)


    for (ma <- mkp.descriptorMatches) {
      val pa = mkp.kpa.toArray.apply(ma.queryIdx).pt
      val pb = mkp.kpb.toArray.apply(ma.trainIdx).pt
      Imgproc.line(imgBTrack, pa, pb, new Scalar(5, 255, 255, 60))
      Imgproc.circle(imgBTrack, pa, 4, new Scalar(5, 125, 125, 60))
      Imgproc.circle(imgBTrack, pb, 4, new Scalar(255, 0, 0))
    }

    imgBTrack
  }

  def findAndDrawFeatures(ima: Mat): Mat = {
    val imgGray = colorToGray(ima)
    val kpd = kpext.detectAndDescribe(imgGray)

    val imgOut = ima.clone()
    kpd.kp.toArray foreach { k => Imgproc.circle(imgOut, k.pt, 3, new Scalar(0, 200, 0), -1) }
    imgOut
  }

  def findAndDrawTracksBoth(ima: Mat, imb: Mat): Mat = {
    drawTracksBoth(ima, imb, findKeypointMatches(ima, imb))
  }

  def findAndDrawTracks(ima: Mat, imb: Mat): Mat = {
    drawTracks(imb, findKeypointMatches(ima, imb))
  }

  def findAndDrawCorrespondences(ima: Mat, imb: Mat): Mat = {
    // drawCorrespondences(ima, imb, findKeypointMatches(ima, imb))
    imb
  }

  def estimateTransform(mkp: MatchingKeypoints) = {
    val MatchingKeypoints(a, b, c) = mkp

    val (ax, ay) = ((0.0, 0.0) /: mkp.descriptorMatches) { case ((xx, yy), aa) =>
      val pa = mkp.kpa.toArray.apply(aa.queryIdx).pt
      val pb = mkp.kpb.toArray.apply(aa.trainIdx).pt

      (xx + pb.x - pa.x, yy + pb.y - pa.y)
    }
    val N = mkp.descriptorMatches.size
    // println(f"${ax / N}%7.2f ${ay / N}%7.2f")
    (ax / N, ay / N)
  }

  def drawTransform(img: Mat, mkp: MatchingKeypoints) = {
    val (ax, ay) = estimateTransform(mkp)
    Imgproc.line(img, new Point(50, 50), new Point(50 + ax, 50 + ay), new Scalar(0, 255, 5, 60), 5)
    img
  }

}

case class ImageAndDescriptors(image: Mat, kps: ExtractedKeypoints)