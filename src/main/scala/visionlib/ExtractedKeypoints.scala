package visionlib

import org.opencv.core.{Mat, MatOfKeyPoint}

case class ExtractedKeypoints(kp: MatOfKeyPoint, desc: Mat)
