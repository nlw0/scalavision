import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d._

object OrbExtraction extends App with UtilityFunctions {


  // Detects keypoints and extracts descriptors in a given image of type Mat.
  def detectAndExtract(mat: Mat) = {
    // A special container class for KeyPoint.
    val keyPoints = new MatOfKeyPoint
    // We're using the SURF detector.
    val detector = FeatureDetector.create(FeatureDetector.ORB)
    detector.detect(mat, keyPoints)

    println(s"There were ${keyPoints.toArray.size} KeyPoints detected")

    // Let's just use the best KeyPoints.
    val sorted = keyPoints.toArray.sortBy(_.response).reverse.take(50)
    // There isn't a constructor that takes Array[KeyPoint], so we unpack
    // the array and use the constructor that can take any number of
    // arguments.
    val bestKeyPoints: MatOfKeyPoint = new MatOfKeyPoint(sorted: _*)

    // We're using the SURF descriptor.
    val extractor = DescriptorExtractor.create(DescriptorExtractor.ORB)
    val descriptors = new Mat
    extractor.compute(mat, bestKeyPoints, descriptors)

    println(s"${descriptors.rows} descriptors were extracted, each with dimension ${descriptors.cols}")

    (bestKeyPoints, descriptors)
  }


  println(System.getProperty("java.library.path"))
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val imageFilename = getClass.getResource("/mahakala_mask_3d_____hdr_cross_eye_stereoscopy_by_zour-d4yffzo.jpg").getPath
  //  val imageFilename = getClass.getResource("/buska.jpg").getPath
  // val imageFilename = getClass.getResource("/left05.jpg").getPath
  val inputImage = Imgcodecs.imread(imageFilename, Imgcodecs.IMREAD_GRAYSCALE)

  val ima = inputImage.colRange(0, inputImage.cols() / 2 - 1)
  val imb = inputImage.colRange(inputImage.cols() / 2, inputImage.cols() - 1)

  // Detect KeyPoints and extract descriptors.
  val (kpa, desca) = detectAndExtract(ima)
  val (kpb, descb) = detectAndExtract(imb)

  val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
  // A special container class for DMatch.
  val dmatches = new MatOfDMatch
  // The backticks are because "match" is a keyword in Scala.
  matcher.`match`(desca, descb, dmatches)

  // Visualize the matches and save the visualization.
  val correspondenceImage = new Mat
  Features2d.drawMatches(ima, kpa, imb, kpb, dmatches, correspondenceImage)

  val resultImg = new Mat
  Features2d.drawKeypoints(ima, kpa, resultImg)

  Imgcodecs.imwrite("orb.png", correspondenceImage)
}
