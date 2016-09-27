import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.util.ByteString
import org.opencv.core.{Mat, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs
import visionlib.{Tracker, TestKeypointExtractor}

import scala.concurrent.{ExecutionContextExecutor, Future}


trait ImageProcessingServer {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  val userRoutes =
    path("camera") {
      get {
        val stream = getClass.getResourceAsStream("/index.html")
        val lines = scala.io.Source.fromInputStream(stream).mkString
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, lines))
      }
    } ~ path("test") {
      get {
        val imageAfilename = "/home/nlw/buska.jpg"
        val imageBfilename = "/home/nlw/buska.jpg"

        val imgA = TestKeypointExtractor.openImage(imageAfilename)
        val imgB = TestKeypointExtractor.openImage(imageBfilename)
        val mkp = TestKeypointExtractor.findKeypointMatches(imgA, imgB)
        //val img = TestKeypointExtractor.drawCorrespondences(imgA, imgB, mkp)
        val img = TestKeypointExtractor.drawTracksBoth(imgA, imgB, mkp)
        val yowza = new MatOfByte

        Imgcodecs.imencode(".jpg", img, yowza)

        complete(HttpEntity(MediaTypes.`image/jpeg`, yowza.toArray))
      }
    } ~ uploadFile

  def uploadFile: Route = path("upload") {
    fileUpload("img") { case (metadata, byteSource) =>
      val aa: Future[ByteString] = byteSource.runReduce((a, b) => a concat b)
      val bb: Future[Array[Byte]] = aa map { bs =>
        val tt = System.currentTimeMillis
        println(s"$tt chegou um arquivo")

        val aa = imageFromByteString(bs, Imgcodecs.IMREAD_COLOR)

        val ii = Tracker.memtrack(aa)

        imageToByteArray(ii.getOrElse(aa))
      }

      onSuccess(bb) { ww =>
        val tt = System.currentTimeMillis
        println(s"$tt retornando")
        complete(HttpEntity(MediaTypes.`image/jpeg`, ww))
      }
    }
  }

  def imageFromByteString(bs: ByteString, flags: Int = Imgcodecs.IMREAD_GRAYSCALE): Mat = {
    val mob = new MatOfByte
    mob.fromArray(bs.toArray: _*)
    Imgcodecs.imdecode(mob, flags)
  }

  def imageToByteArray(ii: Mat): Array[Byte] = {
    val output = new MatOfByte
    Imgcodecs.imencode(".jpg", ii, output)
    output.toArray
  }

  val corsHeaders = List(
    RawHeader("Access-Control-Allow-Origin", "*"),
    RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"),
    RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
  )

  val corsRoutes = respondWithHeaders(corsHeaders) {
    userRoutes
  }

  val route = corsRoutes
}
