package visionlib

import org.opencv.core.Mat

case class ImageAndDescriptors(image: Mat, kps: ExtractedKeypoints)
