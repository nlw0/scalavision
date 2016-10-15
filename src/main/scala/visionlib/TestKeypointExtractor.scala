package visionlib

import java.util

import org.opencv.calib3d.Calib3d
import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector}
import org.opencv.imgproc.Imgproc

import scala.collection.JavaConversions._

object kpext {
  val kpext = new KeypointExtractor(FeatureDetector.PYRAMID_AKAZE, DescriptorExtractor.AKAZE)
  val detectAndDescribe = kpext.detectAndDescribe _
}

trait TestKeypointExtractor extends UtilityFunctions {

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

  def drawTransformsBoth(ima: Mat, imb: Mat, mkp: MatchingKeypoints): Mat = {

    val H = homographyFromMatches(mkp)

    val imat = new Mat()
    val imbt = new Mat()

    Imgproc.warpPerspective(ima, imat, H, ima.size)
    Imgproc.warpPerspective(imb, imbt, H.inv(), ima.size)

    concatenateImages(imbt, imat)
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

  def findAndDrawTracksBoth(ima: Mat, imb: Mat): Mat = drawTracksBoth(ima, imb, findKeypointMatches(ima, imb))

  def findAndDrawTracks(ima: Mat, imb: Mat): Mat = drawTracks(imb, findKeypointMatches(ima, imb))

  def randomSample[A](x: Iterable[A], n: Int) =
    scala.util.Random.shuffle(x) take n

  def homographyFromMatches(mkp: MatchingKeypoints) = {
    val pta = mkp.descriptorMatches map { aa => mkp.kpa.toArray.apply(aa.queryIdx).pt }
    val ptb = mkp.descriptorMatches map { aa => mkp.kpb.toArray.apply(aa.trainIdx).pt }

    val srcPoints = matFromList(pta)
    val dstPoints = matFromList(ptb)

    Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8.0)
  }

  def matFromList(listOfPoints: Seq[Point]) = {
    val matOfPoints = new MatOfPoint2f()
    matOfPoints.fromList(listOfPoints)
    matOfPoints
  }

  def matFromListNormalized(listOfPoints: Seq[Point]) = {
    val ss = matFromList(listOfPoints)

    for {n <- 0 until ss.rows} {
      val t = Array[Float](0, 0)

      ss.get(n, 0, t)
      t(0) = (t(0) - 400.0f) / 200.0f
      t(1) = (t(1) - 300.0f) / 200.0f
      ss.put(n, 0, t)
    }
    ss
  }

  def drawTranslation(img: Mat, mkp: MatchingKeypoints) = {
    val xx = homographyFromMatches(mkp)
    val ax = xx.get(0, 2).head
    val ay = xx.get(1, 2).head
    Imgproc.line(img, new Point(50, 50), new Point(50 + ax, 50 + ay), new Scalar(0, 255, 5, 60), 5)
    img
  }

}

