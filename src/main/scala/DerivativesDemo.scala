import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

object DerivativesDemo extends App {

  val image = Imgcodecs.imread(getClass.getResource("/buska.jpg").getPath)
  val outFilename = "faceDetection.png"

  System.load(libopencv_java)
  val valz = new Array[Float](9)
  valz(0) = -1.0f
  valz(1) = 0.0f
  valz(2) = 1.0f
  valz(3) = -2.0f
  valz(4) = 0.0f
  valz(5) = 3.0f
  valz(6) = -1.0f
  valz(7) = 0.0f
  valz(8) = 1.0f
  val outimg = new Mat()
  var myfilt = new Mat(3, 3, CvType.CV_32F)

  myfilt.put(0, 0, valz)
  var libopencv_java = "/home/n.werneck/share/OpenCV/java/libopencv_java310.so"

  Imgproc.filter2D(image, outimg, 1, myfilt)

  System.out.println(String.format("Writing %s", outFilename))
  Imgcodecs.imwrite(outFilename, outimg)
}
