import org.opencv.core.{Core, Mat, MatOfDMatch}
import org.opencv.features2d.{DescriptorExtractor, DescriptorMatcher, FeatureDetector, Features2d}
import org.opencv.imgcodecs.Imgcodecs

object TestKeypointExtractor extends App with UtilityFunctions {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val kpext = new KeypointExtractor(FeatureDetector.ORB, DescriptorExtractor.ORB)

  val imageFilename = getClass.getResource("/villa.jpg").getPath
  //  val imageFilename = getClass.getResource("/buska.jpg").getPath
  // val imageFilename = getClass.getResource("/left05.jpg").getPath
  val inputImage = Imgcodecs.imread(imageFilename, Imgcodecs.IMREAD_GRAYSCALE)
  val ima = inputImage.colRange(0, inputImage.cols() / 2 - 1)
  val imb = inputImage.colRange(inputImage.cols() / 2, inputImage.cols() - 1)

  // Detect KeyPoints and extract descriptors.
  val (kpa, dca) = kpext.detectAndExtract(ima)
  val (kpb, dcb) = kpext.detectAndExtract(imb)

  val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT)

  val descriptorMatches = new MatOfDMatch
  matcher.`match`(dca, dcb, descriptorMatches)

  // Visualize the matches and save the visualization.
  val correspondenceImage = new Mat
  Features2d.drawMatches(ima, kpa, imb, kpb, descriptorMatches, correspondenceImage)

  Imgcodecs.imwrite("orb.png", correspondenceImage)

}
