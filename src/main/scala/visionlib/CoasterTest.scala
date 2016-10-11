package visionlib

import org.opencv.calib3d.Calib3d
import org.opencv.core.{Core, Mat, Scalar}

object CoasterTest extends VisionApp with TestKeypointExtractor {

  val INPUT_SIZE = 400

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


  for {((imb, bmkp), nn) <- matches} {

    val pta = bmkp.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map { aa =>
      bmkp.kpa.toArray.apply(aa.queryIdx).pt
    }
    val ptb = bmkp.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map { aa =>
      bmkp.kpb.toArray.apply(aa.trainIdx).pt
    }

    val srcPoints = matFromList(pta)
    val dstPoints = matFromList(ptb)

    val H = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8.0)
    val Hi = Calib3d.findHomography(dstPoints, srcPoints, Calib3d.LMEDS, 8.0)
    val g = Hi.inv
    val gg = g.get(2,2)(0)
    val ggg = new Mat()
    Core.divide(g, Scalar.all(gg), ggg)

    val j = H.inv
    val jj = j.get(2,2)(0)
    val jjj = new Mat()
    Core.divide(j, Scalar.all(jj), jjj)

    println(H.dump)
    println(ggg.dump)
    println(Hi.dump)
    println(jjj.dump)
    println()
  }

  for {((imb, bmkp), nn) <- matches
       ((imc, cmkp), mm) <- matches
       if nn < mm} {

    val pta = bmkp.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map { aa =>
      bmkp.kpa.toArray.apply(aa.trainIdx).pt
    }
    val ptb = cmkp.descriptorMatches filter { aa => pointsSeenEverywhere contains aa.queryIdx } map { aa =>
      cmkp.kpb.toArray.apply(aa.trainIdx).pt
    }

    val srcPoints = matFromList(pta)
    val dstPoints = matFromList(ptb)

    val H = Calib3d.findHomography(srcPoints, dstPoints, Calib3d.LMEDS, 8.0)
    val Hi = Calib3d.findHomography(dstPoints, srcPoints, Calib3d.LMEDS, 8.0)

    val g = Hi.inv
    val gg = g.get(2,2)(0)
    val ggg = new Mat()
    Core.divide(g, Scalar.all(gg), ggg)

    val j = H.inv
    val jj = j.get(2,2)(0)
    val jjj = new Mat()
    Core.divide(j, Scalar.all(jj), jjj)

    println(H.dump)
    println(ggg.dump)
    println(Hi.dump)
    println(jjj.dump)
    println()
  }

  def imagePairs = resourcesFromDirectory("/coaster").toStream map openResource

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)
}
