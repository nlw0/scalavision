package visionlib

import org.opencv.calib3d.Calib3d

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

    println(H.dump)
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

    println(H.dump)
  }

  def imagePairs = resourcesFromDirectory("/coaster").toStream map openResource

  def openResource = getFilenameFromResource _ andThen
                     loadImage andThen
                     scaleImageHeight(INPUT_SIZE)
}
