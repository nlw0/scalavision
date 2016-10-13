package visionlib

import org.opencv.calib3d.Calib3d
import org.opencv.core.{Core, CvType, Mat}

object CoasterTestPairs extends VisionApp with TestKeypointExtractor {

  val INPUT_SIZE = 600

  val matches = for (Stream((ima, mm), (imb, nn)) <- imagePairs.take(5).zipWithIndex.combinations(2)) yield {

    val kda = kpext.detectAndDescribe(ima)
    val kdb = kpext.detectAndDescribe(imb)

    val mkp = matchKeypoints(kda, kdb)

    ((ima, mm), (imb, nn), mkp)
  }

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

  for (((ima, mm), (imb, nn), mkp) <- matches) {

    val MatchingKeypoints(pta, ptb, _) = mkp

    val lpta = mkp.descriptorMatches map { aa => pta.toArray.apply(aa.queryIdx).pt }
    val lptb = mkp.descriptorMatches map { aa => ptb.toArray.apply(aa.trainIdx).pt }

    val srcPoints = matFromList(lpta)
    val dstPoints = matFromList(lptb)

    for {ss <- List(srcPoints, dstPoints)
         n <- 0 until ss.rows
    } {
      val t = Array[Float](0, 0)

      ss.get(n, 0, t)
      t(0) = (t(0) - 400.0f) / 200.0f
      t(1) = (t(1) - 300.0f) / 200.0f
      ss.put(n, 0, t)
    }

    val H = Calib3d.findHomography(srcPoints, dstPoints, 0, 0.1)
    val Hi = Calib3d.findHomography(dstPoints, srcPoints, 0, 0.1)
    val HHi = new Mat()

    Core.gemm(H, Hi, 1, new Mat(), 0, HHi)

    println(H.dump)
    println(Hi.dump)
    println(HHi.dump)

    val mm = Mat.ones(srcPoints.rows, 3, CvType.CV_32F)
    for (i <- 0 until srcPoints.rows) {
      val t = Array[Float](0, 0)
      srcPoints.get(i, 0, t)
      mm.put(i, 0, t)
    }

    val out = new Mat(12, 3, CvType.CV_32F)

    val hhh = new Mat()
    H.convertTo(hhh, CvType.CV_32F)

    Core.gemm(mm, hhh.t, 1, new Mat(), 0, out)

    for (i <- 0 until srcPoints.rows) {
      val t0 = Array[Float](0)
      val t1 = Array[Float](0)
      val t2 = Array[Float](0)
      val d = Array[Float](0,0)
      out.get(i, 0, t0)
      out.get(i, 1, t1)
      out.get(i, 2, t2)
      dstPoints.get(i, 0, d)

      println(s"${d(0)} ${t0(0)/t2(0)} ${d(0) - t0(0)/t2(0)} ${d(1)} ${t1(0)/t2(0)} ${d(1) - t1(0)/t2(0)}")
    }

    println()
  }

  def imagePairs = resourcesFromDirectory("/coaster").toStream map openResource

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)
}
