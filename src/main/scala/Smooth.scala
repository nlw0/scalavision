import java.awt.{Graphics, Panel}
import javax.swing.{ImageIcon, JFrame}

import org.opencv.core.{Core, CvType, Mat, Size}
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture


object Smooth extends App {

  def smooth(image: Mat): Mat = {
    val outImage: Mat = image.clone()
    Imgproc.GaussianBlur(image, outImage, new Size(0, 0), 3.0)
    outImage
  }

  def save_image(filename: String, image: Mat) =
    Imgcodecs.imwrite(filename, image)

  def open_image_bgr(filename: String) = {
    Option(Imgcodecs.imread(filename)) map {
      x: Mat =>
        Imgproc.cvtColor(x, x, Imgproc.COLOR_RGB2BGR)
        x
    }
  }

  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

//  val filename = args(0)
//  println(s"smurfando $filename")
//
//  val imageOpt: Option[Mat] = open_image_bgr(filename)
//
//  val smoothedOpt: Option[Mat] = imageOpt map smooth
//
//  for (img <- smoothedOpt) {
//    save_image("/home/nlw/aaa.jpg", img)
//
//    val frame: JFrame = new JFrame("Figure track")
//    val panel = new ShowImage(img)
//    frame.getContentPane.add(panel)
//    frame.setSize(500, 500)
//    frame.setVisible(true)
//  }


  val videoCap = new VideoCapture(0)
  val aa: Mat = new Mat(1000, 1000, CvType.CV_8UC3)

  Thread.sleep(100)
  videoCap.grab()
  videoCap.retrieve(aa)

  val frame: JFrame = new JFrame("yowza")
  val panel = new ShowImage(aa)
  frame.getContentPane.add(panel)
  frame.setSize(500, 500)
  frame.setVisible(true)

  while (true) {
    Thread.sleep(100)
    videoCap.grab()
    val bb: Mat = new Mat(1000, 1000, CvType.CV_8UC3)
    videoCap.retrieve(bb)
    panel.update(bb)
    println(s"oii ${bb.get(0,0)}")
  }
}
