package visionlib

import breeze.linalg._
import org.opencv.core.{CvType, Mat}

object CoasterTestPairs extends VisionApp with TestKeypointExtractor with CoasterData {

  val nImages = 5

  val myImages = allImages.flatten.take(nImages).toVector

  val descriptors = myImages map { im => kpext.detectAndDescribe(im) }

  val matches = (for {mm <- 0 until nImages
                      nn <- 0 until nImages
                      if mm != nn} yield {
    val kda = descriptors(mm)
    val kdb = descriptors(nn)
    (mm, nn) -> matchKeypoints(kda, kdb)
  }).toMap

  val allHomos = Mat.eye(3 * nImages, 3 * nImages, CvType.CV_64F)

  val allHomosB = DenseMatrix.eye[Double](3 * nImages)

  for {m <- 0 until nImages
       n <- 0 until nImages
       if m != n} {

    val mkp = matches((m, n))

    val MatchingKeypoints(pta, ptb, _) = mkp

    val H = mkp.homography

    val subMat = allHomos.colRange(n * 3, n * 3 + 3).rowRange(m * 3, m * 3 + 3)
    H.copyTo(subMat)

    val hh = Array.fill[Double](9)(0.0)
    H.get(0, 0, hh)
    val coisa = new DenseMatrix(3, 3, hh)
    println(H)
    println(coisa)
    println()

  }

  println(allHomos.dump)
}