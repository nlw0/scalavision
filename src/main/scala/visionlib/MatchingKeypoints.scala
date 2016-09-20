package visionlib

import org.opencv.core.{DMatch, MatOfKeyPoint}

case class MatchingKeypoints(kpa: MatOfKeyPoint, kpb: MatOfKeyPoint, descriptorMatches: Seq[DMatch])
