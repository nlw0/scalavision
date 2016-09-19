package visionlib

import org.opencv.core.{MatOfDMatch, MatOfKeyPoint}

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: MatOfDMatch)
