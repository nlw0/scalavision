import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.opencv.core.Core

import scala.io.StdIn

object Boot extends App with ImageProcessingServer {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  // Http().bindAndHandle(routes, "0.0.0.0", 9000)

  println(Core.NATIVE_LIBRARY_NAME)
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done
}
