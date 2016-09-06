import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature
import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, FeatureDetector, Features2d}

class KeypointExtractor(detectorType: Int = FeatureDetector.ORB, descriptorType: Int = DescriptorExtractor.ORB) {

  val detector = FeatureDetector.create(detectorType)
  val extractor = DescriptorExtractor.create(descriptorType)

  def detectAndExtract(image: Mat) = {
    val keypoints: MatOfKeyPoint = extractKeypointsFromImage(image)

    val bestKeypoints: MatOfKeyPoint = selectKeypoints(keypoints, 200)

    val descriptors = new Mat
    extractor.compute(image, bestKeypoints, descriptors)

    (bestKeypoints, descriptors)
  }

  def selectKeypoints(keyPoints: MatOfKeyPoint, number: Int = 50): MatOfKeyPoint = {
    def sortedKeyPoints = keyPoints.toArray.sortBy(-_.response).take(number)

    new MatOfKeyPoint(sortedKeyPoints: _*)
  }

  def extractKeypointsFromImage(image: Mat): MatOfKeyPoint = {
    val keyPoints = new MatOfKeyPoint
    detector.detect(image, keyPoints)
    keyPoints
  }
}
