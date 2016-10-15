package visionlib

import org.opencv.calib3d.Calib3d
import org.opencv.core.{CvType, Mat, MatOfPoint2f}

object CoasterTestPairs extends VisionApp with TestKeypointExtractor {

  val INPUT_SIZE = 600

  val nImages = 5

  val myImages = allImages.take(nImages).toVector

  val descriptors = myImages map { im => kpext.detectAndDescribe(im) }

  val matches = (for (Seq(mm, nn) <- (0 to 4).combinations(2)) yield {

    val ima = myImages(mm)
    val imb = myImages(nn)

    val kda = descriptors(mm)
    val kdb = descriptors(nn)

    val mkp = matchKeypoints(kda, kdb)

    (mm, nn) -> mkp
  }).toMap

  //  for (((ima, mm), (imb, nn), mkp) <- matches) {
  //
  //    val MatchingKeypoints(aa, bb, dd) = mkp
  //
  //    val outImg = drawTracksBoth(ima, imb, mkp)
  //    val outImgTrans = drawTransformsBoth(ima, imb, mkp)
  //
  //    def filenameTrans = { num: Int => f"/home/nlw/coisatrans-$num%04d.png" }
  //
  //    saveToFile(filenameTrans(mm*100+nn))(concatenateImagesVertical(outImg, outImgTrans))
  //  }

  val allHomos = Mat.eye(3 * nImages, 3 * nImages, CvType.CV_32F)

  for {m <- 0 until nImages
       n <- 0 until nImages
       if m != n} {

    val mkp = if (m < n) matches((m, n)) else matches((n, m))

    val MatchingKeypoints(pta, ptb, _) = mkp

    val lpta = mkp.descriptorMatches map { aa => pta.toArray.apply(aa.queryIdx).pt }
    val lptb = mkp.descriptorMatches map { aa => ptb.toArray.apply(aa.trainIdx).pt }

    val srcPoints = matFromListNormalized(lpta)
    val dstPoints = matFromListNormalized(lptb)

    val H = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8)

    println(H.dump)
  }

  def allImages = resourcesFromDirectory("/coaster").toStream map openResource

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)

  def homoMatFromWeird(srcPoints: MatOfPoint2f) = {
    val mm = Mat.ones(srcPoints.rows, 3, CvType.CV_32F)
    for (i <- 0 until srcPoints.rows) {
      val t = Array[Float](0, 0)
      srcPoints.get(i, 0, t)
      mm.put(i, 0, t)
    }
    mm
  }

}



//
//    println(H.dump)
//
//    val mm = homoMatFromWeird(srcPoints)
//    val nn = homoMatFromWeird(dstPoints)
//
//    val out = new Mat()
//    val hhh = new Mat()
//    val hhhi = new Mat()
//
//    H.convertTo(hhh, CvType.CV_32F)
//    Hi.convertTo(hhhi, CvType.CV_32F)
//
//    Core.gemm(mm, hhh.t, 1, new Mat(), 0, out)
//    Core.gemm(nn, hhhi.t, 1, new Mat(), 0, out)
//
//    for (i <- 0 until srcPoints.rows) {
//      val t0 = Array[Float](0)
//      val t1 = Array[Float](0)
//      val t2 = Array[Float](0)
//      out.get(i, 0, t0)
//      out.get(i, 1, t1)
//      out.get(i, 2, t2)
//      val u = t0(0) / t2(0)
//      val v = t1(0) / t2(0)
//
//      val d = Array[Float](0, 0)
//      dstPoints.get(i, 0, d)
//
//      println(s"${d(0)} ${u} ${d(0) - u} ${d(1)} ${v} ${d(1) - v}")


//
//
//println("====")
//
//for (i <- 0 until dstPoints.rows) {
//val t0 = Array[Float](0)
//val t1 = Array[Float](0)
//val t2 = Array[Float](0)
//out.get(i, 0, t0)
//out.get(i, 1, t1)
//out.get(i, 2, t2)
//val u = t0(0) / t2(0)
//val v = t1(0) / t2(0)
//
//val d = Array[Float](0, 0)
//srcPoints.get(i, 0, d)
//
//println(s"${d(0)} ${u} ${d(0) - u} ${d(1)} ${v} ${d(1) - v}")
