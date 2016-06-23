import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

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

  def findKeypointMatches(ima: Mat, imb: Mat): (MatOfKeyPoint, MatOfKeyPoint, MatOfDMatch) = {
    val (kpa, dca) = kpext.detectAndExtract(ima)
    val (kpb, dcb) = kpext.detectAndExtract(imb)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    val descriptorMatches = new MatOfDMatch
    matcher.`match`(dca, dcb, descriptorMatches)
    (kpa, kpb, descriptorMatches)
  }

  def findAndDrawTracks(ima: Mat, imb: Mat): Mat = {
    val (kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: MatOfDMatch) = findKeypointMatches(ima, imb)

    // Visualize the matches and save the visualization.
    val correspondenceImage = new Mat()

    val imgatrack = new Mat()
    ima.copyTo(imgatrack)
    val imgbtrack = new Mat()
    imb.copyTo(imgbtrack)
    //    for (kp <- kpa.toArray) {
    //      Imgproc.circle(imgatrack, kp.pt, 4, new Scalar(1.0,1.0,0.5))
    //    }
    //    for (kp <- kpb.toArray) {
    //      Imgproc.circle(imgatrack, kp.pt, 4, new Scalar(0.5,1.0,1.0))
    //    }
    for (aa <- descriptorMatches.toArray) {
      val pa = kpa.toArray.apply(aa.queryIdx).pt
      val pb = kpb.toArray.apply(aa.trainIdx).pt
      Imgproc.line(imgatrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.line(imgbtrack, pa, pb, new Scalar(255, 255, 5, 60))
      Imgproc.circle(imgatrack, pb, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgbtrack, pa, 4, new Scalar(125, 125, 5, 60))
      Imgproc.circle(imgatrack, pa, 4, new Scalar(0, 0, 255))
      Imgproc.circle(imgbtrack, pb, 4, new Scalar(0, 0, 255))
    }

    concatenateImages(imgatrack, imgbtrack)

  }

  def findAndDrawCorrespondences(ima: Mat, imb: Mat): Mat = {
    val (kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: MatOfDMatch) = findKeypointMatches(ima, imb)

    // Visualize the matches and save the visualization.
    val correspondenceImage = new Mat()
    Features2d.drawMatches(ima, kpa, imb, kpb, descriptorMatches, correspondenceImage)
    correspondenceImage
  }

  //  val imageAFilename = System.getProperty("imageA")
  //  val imageBFilename = System.getProperty("imageB")
  //  val imageA = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868262.621668.png"
  //  val imageB = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868263.053350.png"
  //  val correspondenceImage = findAndDrawCorrespondences(imageA, imageB)
  //  Imgcodecs.imwrite("orb.png", correspondenceImage)
}
