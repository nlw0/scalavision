package visionlib

import breeze.linalg._

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

  val allHomos = DenseMatrix.eye[Double](3 * nImages)

  for {m <- 0 until nImages
       n <- 0 until nImages
       if m != n} {

    val mkp = matches((m, n))

    val MatchingKeypoints(pta, ptb, _) = mkp

    val H = mkp.homography

    allHomos(m * 3 to m * 3 + 2, n * 3 to n * 3 + 2) := H
    println(H)
    println()
  }

  println(allHomos)
}