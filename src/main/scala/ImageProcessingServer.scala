import java.io.{BufferedInputStream, FileOutputStream}
import java.net.URL
import java.util.UUID

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.Materializer
import akka.util.ByteString
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import org.opencv.core.{Core, Mat, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs

import scala.util.Random
import scala.io.StdIn


trait ImageProcessingServer {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  // streams are re-usable so we can define it here
  // and use it for every request
  val numbers = Source.fromIterator(() =>
    Iterator.continually(Random.nextInt()))

  val route =
    path("random") {
      get {
        complete(
          HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            // transform each number to a chunk of bytes
            numbers.map(n => ByteString(s"$n\n"))
          )
        )
      }
    } ~ path("test") {
      get {
        //          val imageFilename = "/home/n.werneck/buska.jpg"
        //        val imageA = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868262.621668.png"
        //        val imageB = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868263.053350.png"
        val imageA = "/home/nlw/buska.jpg"
        val imageB = "/home/nlw/buska.jpg"

        val img = TestKeypointExtractor.findAndDrawCorrespondences(
          TestKeypointExtractor.openImage(imageA), TestKeypointExtractor.openImage(imageB))
        val yowza = new MatOfByte

        Imgcodecs.imencode(".jpg", img, yowza)

        complete(HttpEntity(MediaTypes.`image/jpeg`, yowza.toArray))
      }
    } ~ uploadFile

  def uploadFile: Route = {
    path("upload") {
      fileUpload("img") {
        case (metadata, byteSource) =>

          val aa: Future[ByteString] = byteSource.runReduce((a, b) => a concat b)
          val bb: Future[Array[Byte]] = aa map (
            bs => {
              val qq: Array[Byte] = bs.toArray
              val bb = new MatOfByte
              bb.fromArray(qq: _*)
              val aa = Imgcodecs.imdecode(bb, Imgcodecs.IMREAD_GRAYSCALE)
              val ii: Mat = TestKeypointExtractor.findAndDrawCorrespondences(aa, aa)
              val yowza = new MatOfByte
              Imgcodecs.imencode(".jpg", ii, yowza)
              yowza.toArray
            }
            )

          onSuccess(bb) {
            ww => complete(HttpEntity(MediaTypes.`image/jpeg`, ww))
          }






        //        case (metadata, byteSource) =>
        //          val sumF: Future[Int] =
        //          // sum the numbers as they arrive so that we can
        //          // accept any size of file
        //            byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
        //              .mapConcat(_.utf8String.split(",").toVector)
        //              .map(_.toInt)
        //              .runFold(0) { (acc, n) => acc + n }
        //          onSuccess(sumF) { sum => complete(s"Sum: $sum") }
      }
    }
  }
}


//  private def processFile(filePath: String, fileData: Multipart.FormData) = {
//    val fileOutput = new FileOutputStream(filePath)
//    fileData.parts.mapAsync(1) { bodyPart ⇒
//      def writeFileOnLocal(array: Array[Byte], byteString: ByteString): Array[Byte] = {
//        val byteArray: Array[Byte] = byteString.toArray
//        fileOutput.write(byteArray)
//        array ++ byteArray
//      }
//      bodyPart.entity.dataBytes.runFold(Array[Byte]())(writeFileOnLocal)
//    }.runFold(0)(_ + _.length)
//  }

