import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.util.ByteString
import org.opencv.core.{Mat, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs

import scala.concurrent.{ExecutionContextExecutor, Future}


trait ImageProcessingServer {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  val route =
    path("camera") {
      get {
        val stream = getClass.getResourceAsStream("/index.html")
        val lines = scala.io.Source.fromInputStream(stream).mkString
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`, lines
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
      }
    }
  }
}
