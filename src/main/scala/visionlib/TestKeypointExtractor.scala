package visionlib

import java.util

import org.opencv.core.{Point, _}
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

    val xx = (for {Array(ma, mb) <- descriptorMatches map (_.toArray)
                   if ma.distance < MAGIC_LOWE_THRESHOLD * mb.distance} yield ma).toList

    MatchingKeypoints(kda.kp, kdb.kp, xx)
  }

  def drawTracksBothOut(ima: Mat, imb: Mat, mkp: MatchingKeypoints, outl: Seq[Boolean]): Mat = {
    val imgATrack = new Mat()
    ima.copyTo(imgATrack)
    val imgBTrack = new Mat()
    imb.copyTo(imgBTrack)

    def linecolor(x: Boolean) = if (x) new Scalar(205, 255, 5, 0) else new Scalar(5, 180, 205, 0)

    for ((aa, oo) <- mkp.descriptorMatches zip outl) {
      val pa = mkp.kpa.toArray.apply(aa.queryIdx).pt
      val pb = mkp.kpb.toArray.apply(aa.trainIdx).pt
      Imgproc.line(imgATrack, pa, pb, linecolor(oo))
      Imgproc.line(imgBTrack, pa, pb, linecolor(oo))
      Imgproc.circle(imgATrack, pb, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgBTrack, pa, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgATrack, pa, 4, new Scalar(0, 0, 255))
      Imgproc.circle(imgBTrack, pb, 4, new Scalar(0, 0, 255))
    }

    concatenateImages(imgATrack, imgBTrack)
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

    val H = mkp.homographyMat

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

  def drawTranslation(img: Mat, mkp: MatchingKeypoints) = {
    val xx = mkp.homographyMat
    val ax = xx.get(0, 2).head * 150.0f
    val ay = xx.get(1, 2).head * 150.0f
    println(s"trans $ax $ay")
    Imgproc.line(img, new Point(50, 50), new Point(50 + ax, 50 + ay), new Scalar(0, 255, 5, 60), 5)
    img
  }

}

