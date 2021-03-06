package visionlib

import org.opencv.core._
import org.opencv.features2d.{DescriptorExtractor, FeatureDetector}

class KeypointExtractor(detectorType: Int = FeatureDetector.ORB, descriptorType: Int = DescriptorExtractor.ORB)
  extends UtilityFunctions {

  val detector = FeatureDetector.create(detectorType)

  if (detectorType == FeatureDetector.ORB)
    detector.read(getClass.getResource("orb_parameters.yaml").getPath)

  val descriptor = DescriptorExtractor.create(descriptorType)

  def detectAndDescribe(image: Mat) = {
    val keypoints: MatOfKeyPoint = extractKeypointsFromImage(image)

    val bestKeypoints: MatOfKeyPoint = selectKeypoints(200)(keypoints)

    val descriptors = new Mat
    descriptor.compute(image, bestKeypoints, descriptors)

    ExtractedKeypoints(bestKeypoints, descriptors)
  }

  def selectKeypoints(number: Int = 50)(keyPoints: MatOfKeyPoint): MatOfKeyPoint = {
    def sortedKeyPoints = keyPoints.toArray sortBy (-_.response) take number

    new MatOfKeyPoint(sortedKeyPoints: _*)
  }

  def extractKeypointsFromImage(image: Mat): MatOfKeyPoint = {
    val keyPoints = new MatOfKeyPoint
    detector.detect(image, keyPoints)
    keyPoints
  }
}

