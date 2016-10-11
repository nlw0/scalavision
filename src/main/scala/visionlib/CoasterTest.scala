package visionlib

import org.opencv.calib3d.Calib3d
import org.opencv.core.{Core, CvType, Mat}

object CoasterTest extends VisionApp with TestKeypointExtractor {

  val INPUT_SIZE = 600

  val ima #:: ii = imagePairs
  // .take(5)
  val kda = kpext.detectAndDescribe(ima)

  val matches = for ((imb, nn) <- ii.zipWithIndex) yield {

    val kdb = kpext.detectAndDescribe(imb)

    val mkp = matchKeypoints(kda, kdb)

    ((imb, mkp), nn)
  }


  val pointsSeenEverywhere = matches map { case ((_, mkp), _) =>
    mkp.descriptorMatches.map(_.queryIdx).toSet
  } reduce (_ intersect _)

  println(pointsSeenEverywhere.toSeq.sorted)

  for (((imb, mkp), nn) <- matches) {

    val MatchingKeypoints(aa, bb, dd) = mkp
    val newdd = dd.filter({ d => pointsSeenEverywhere contains d.queryIdx })
    val newmkp = MatchingKeypoints(aa, bb, newdd)

    val outImg = drawTracksBoth(ima, imb, newmkp)
    val outImgTrans = drawTransformsBoth(ima, imb, newmkp)

    def filename = { num: Int => f"/home/nlw/coisa-$num%02d.png" }

    def filenameTrans = { num: Int => f"/home/nlw/coisatrans-$num%02d.png" }

    // saveToFile(filename(nn))(outImg)
    saveToFile(filenameTrans(nn))(concatenateImagesVertical(outImg, outImgTrans))
  }

  val pta = matches.head._1._2.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map {
    aa => matches.head._1._2.kpa.toArray.apply(aa.queryIdx).pt
  }

  val imagesWithPointsHead = List((ima, pta))

  val coisa = for {((imb, bmkp), nn) <- matches} yield {
    val ptb = bmkp.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map { aa =>
      bmkp.kpb.toArray.apply(aa.trainIdx).pt
    }
    (imb, ptb)
  }

  val imagesWithPoints = imagesWithPointsHead ++ coisa

  for {List((ima, pta), (imb, ptb)) <- imagesWithPoints.combinations(2)} {

    val srcPoints = matFromList(pta)
    val dstPoints = matFromList(ptb)

    for {ss <- List(srcPoints, dstPoints)
         n <- 0 until ss.rows
    } {
      val t = Array[Float](0, 0)

      ss.get(n, 0, t)
      t(0) = (t(0) - 400.0f) / 200.0f
      t(1) = (t(1) - 300.0f) / 200.0f
      ss.put(n, 0, t)
    }

    println(srcPoints.dump)
    println(dstPoints.dump)

    val H = Calib3d.findHomography(srcPoints, dstPoints, 0, 8.0)
    val mm = Mat.ones(srcPoints.rows, 3, CvType.CV_32F)

    for (i <- 0 until 12) {
      val t = Array[Float](0, 0)
      srcPoints.get(i, 0, t)
      mm.put(i, 0, t)
    }

    val out = new Mat(12, 3, CvType.CV_32F)

    val hhh = new Mat()
    H.convertTo(hhh, CvType.CV_32F)

    Core.gemm(mm, hhh.t, 1, new Mat(), 0, out)

    println(out.dump)
  }

  def imagePairs = resourcesFromDirectory("/coaster").toStream map openResource

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)
}
