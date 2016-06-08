import org.opencv.core.{Core, Mat, MatOfDMatch}
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs

object TestKeypointExtractor extends App with UtilityFunctions {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val kpext = new KeypointExtractor(FeatureDetector.ORB, DescriptorExtractor.ORB)

  def openImage(fileName: String): Mat = {
    val imageFilename = System.getProperty(fileName)
    Imgcodecs.imread(imageFilename, Imgcodecs.IMREAD_GRAYSCALE)
  }

  val ima = openImage("imageA")
  val imb = openImage("imageB")

  // Detect KeyPoints and extract descriptors.
  val (kpa, dca) = kpext.detectAndExtract(ima)
  val (kpb, dcb) = kpext.detectAndExtract(imb)

  val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

  val descriptorMatches = new MatOfDMatch
  matcher.`match`(dca, dcb, descriptorMatches)

  // Visualize the matches and save the visualization.
  val correspondenceImage = new Mat()
  Features2d.drawMatches(ima, kpa, imb, kpb, descriptorMatches, correspondenceImage)

  Imgcodecs.imwrite("orb.png", correspondenceImage)
}
