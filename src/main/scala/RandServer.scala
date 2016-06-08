import java.io.BufferedInputStream
import java.net.URL

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.opencv.core.{Core, MatOfByte}
import org.opencv.imgcodecs.Imgcodecs

import scala.util.Random
import scala.io.StdIn

object RandServer {

  def main(args: Array[String]) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

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
          val imageA = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868262.621668.png"
          val imageB = "/home/n.werneck/DATA/TUM/rgbd_dataset_freiburg2_desk/rgb/1311868263.053350.png"

          val img = TestKeypointExtractor.findAndDrawCorrespondences(imageA, imageB)
          val yowza = new MatOfByte

          Imgcodecs.imencode(".jpg", img, yowza)

          complete(
            HttpEntity(MediaTypes.`image/jpeg`, yowza.toArray)
          )
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
  }
}