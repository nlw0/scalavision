package visionlib

import breeze.linalg._
import breeze.linalg.svd.SVD

object BreezeTest extends App {
  val xx = DenseVector(1, 2, 3, 4, 5.0)
  val yy = DenseVector.rand(5)
  yy(0 to 4) := yy / norm(yy)

  val ww = yy * xx.t

  val SVD(u, s, v) = svd(ww)

  println(s)
  println(v(0, ::).t / v(0, 0))
}