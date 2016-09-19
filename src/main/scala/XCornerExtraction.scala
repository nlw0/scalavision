import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import visionlib.UtilityFunctions

object XCornerExtraction extends App with UtilityFunctions {

  println(System.getProperty("java.library.path"))
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  //val imageFilename = getClass.getResource("/buska.jpg").getPath
  val imageFilename = getClass.getResource("/left05.jpg").getPath
  val imageFloat = asType(Imgcodecs.imread(imageFilename, Imgcodecs.IMREAD_GRAYSCALE), CvType.CV_32F)

  val dx = applyFilter(imageFloat, cannyFilter)
  val dxx = applyFilter(dx, cannyFilter)
  val dy = applyFilter(imageFloat, cannyFilter.t())
  val dyy = applyFilter(dy, cannyFilter.t())
  val dxy = applyFilter(dx, cannyFilter.t())

  val im_denom = new Mat()
  Core.subtract(dxx.mul(dyy), dxy.mul(dxy), im_denom)

  val im_denom_min = new Mat()
  Imgproc.erode(im_denom, im_denom_min, Mat.ones(3,3, CvType.CV_8U))

  //    im_classify = (im_denom < -100) * ((imx ** 2 + imy ** 2) < -2 * im_denom)
  //    im_loci = im_classify * cv2.compare(im_denom, im_denom_min, cv2.CMP_LE)
  //
  //    yy, xx = np.where(im_loci)
  //
  //    ssttnn = np.array([[x + (imy[y, x] * imxy[y, x] - imx[y, x] * imyy[y, x]) / im_denom[y, x],
  //                        y + (imx[y, x] * imxy[y, x] - imy[y, x] * imxx[y, x]) / im_denom[y, x],
  //                        getevec(np.array([[imxx[y, x], imxy[y, x]], [imxy[y, x], imyy[y, x]]]))]
  //                       for y, x in zip(yy, xx)])
  //    return np.array(ssttnn)


  writeFloat(dx, "dx.png")
  writeFloat(dy, "dy.png")
  writeFloat(dxx, "dxx.png")
  writeFloat(dyy, "dyy.png")
  writeFloat(dxy, "dxy.png")

  val xx = asType(im_denom, CvType.CV_8UC3, 0.00001, 128)
  Imgcodecs.imwrite("denom.png", xx)
}
