import org.opencv.core.{Core, Mat, MatOfDMatch}
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs

object TestKeypointExtractor {
  //extends App with UtilityFunctions {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val kpext = new KeypointExtractor(FeatureDetector.ORB, DescriptorExtractor.ORB)

  def openImage(fileName: String): Mat = {
    Imgcodecs.imread(fileName, Imgcodecs.IMREAD_GRAYSCALE)
  }

  def findAndDrawCorrespondences(ima: Mat, imb: Mat): Mat = {
    // Detect KeyPoints and extract descriptors.
    val (kpa, dca) = kpext.detectAndExtract(ima)
    val (kpb, dcb) = kpext.detectAndExtract(imb)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    val descriptorMatches = new MatOfDMatch
    matcher.`match`(dca, dcb, descriptorMatches)

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
