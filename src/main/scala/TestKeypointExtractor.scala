import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: MatOfDMatch)

object TestKeypointExtractor {
  //extends App with UtilityFunctions {
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
    val (kpa, dca) = kpext.detectAndExtract(ima)
    val (kpb, dcb) = kpext.detectAndExtract(imb)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    val descriptorMatches = new MatOfDMatch
    matcher.`match`(dca, dcb, descriptorMatches)
    MatchingKeypoints(kpa, kpb, descriptorMatches)
  }

  def drawTracks(ima: Mat, imb: Mat, mkp: MatchingKeypoints): Mat = {
    val imgATrack = new Mat()
    ima.copyTo(imgATrack)
    val imgBTrack = new Mat()
    imb.copyTo(imgBTrack)

    for (aa <- mkp.descriptorMatches.toArray) {
      val pa = mkp.kpa.toArray.apply(aa.queryIdx).pt
      val pb = mkp.kpb.toArray.apply(aa.queryIdx).pt
      Imgproc.line(imgATrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.line(imgBTrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.circle(imgATrack, pb, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgBTrack, pa, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgATrack, pa, 4, new Scalar(0, 0, 255))
      Imgproc.circle(imgBTrack, pb, 4, new Scalar(0, 0, 255))
    }

    concatenateImages(imgATrack, imgBTrack)
  }

  def findAndDrawFeatures(ima: Mat): Mat = {
    val imgAFt = new Mat()
    val (kpa, dca) = kpext.detectAndExtract(ima)
    Imgproc.cvtColor(ima, imgAFt, Imgproc.COLOR_GRAY2RGB)

    for (pa <- kpa.toArray) {
      Imgproc.circle(imgAFt, pa.pt, 4, new Scalar(0, 0, 255), -1)
    }

    imgAFt
  }

  def drawCorrespondences(ima: Mat, imb: Mat, mkp: MatchingKeypoints): Mat = {
    val correspondenceImage = new Mat()
    Features2d.drawMatches(ima, mkp.kpa, imb, mkp.kpb, mkp.descriptorMatches, correspondenceImage)
    correspondenceImage
  }

  def findAndDrawTracks(ima: Mat, imb: Mat): Mat = {
    drawTracks(ima, imb, findKeypointMatches(ima, imb))
  }

  def findAndDrawCorrespondences(ima: Mat, imb: Mat): Mat = {
    drawCorrespondences(ima, imb, findKeypointMatches(ima, imb))
  }
}