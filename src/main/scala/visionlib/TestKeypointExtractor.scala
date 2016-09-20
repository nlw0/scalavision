package visionlib

import java.util

import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


object TestKeypointExtractor extends UtilityFunctions {
  //extends App with visionlib.UtilityFunctions {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val kpext = new KeypointExtractor(FeatureDetector.ORB, DescriptorExtractor.ORB)

  def openImage(fileName: String): Mat = {
    Imgcodecs.imread(fileName, Imgcodecs.IMREAD_GRAYSCALE)
  }

  def concatenateImages(matA: Mat, matB: Mat) = {
    val m = new Mat(matA.rows(), matA.cols() + matB.cols(), matA.`type`())
    val cols = matA.cols()
    matA.copyTo(m.colRange(0, cols))
    matB.copyTo(m.colRange(cols, cols * 2))
    m
  }

  def findKeypointMatches(ima: Mat, imb: Mat): MatchingKeypoints = {
    val (kpa, dca) = kpext.detectAndDescribe(ima)
    val (kpb, dcb) = kpext.detectAndDescribe(imb)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2)

    val descriptorMatches = new util.ArrayList[MatOfDMatch]()

    // matcher.`match`(dca, dcb, descriptorMatches)

    matcher.knnMatch(dca, dcb, descriptorMatches, 2)

    val xx = for {i <- 0 until descriptorMatches.size
                  ratio = 0.8
                  ma = descriptorMatches.get(i).toArray.apply(0)
                  mb = descriptorMatches.get(i).toArray.apply(1)
                  if ma.distance < ratio * mb.distance
    } yield ma

    MatchingKeypoints(kpa, kpb, xx)
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
      Imgproc.line(imgBTrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.circle(imgBTrack, pa, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgBTrack, pb, 4, new Scalar(0, 0, 255))
    }

    imgBTrack
  }

  def findAndDrawFeatures(ima: Mat): Mat = {
    val imgGray = colorToGray(ima)
    val (kp, _) = kpext.detectAndDescribe(imgGray)

    val imgOut = ima.clone()
    kp.toArray foreach { k => Imgproc.circle(imgOut, k.pt, 3, new Scalar(0, 200, 0), -1) }
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
}
