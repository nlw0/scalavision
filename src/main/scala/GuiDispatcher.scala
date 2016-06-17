import akka.dispatch.{DispatcherPrerequisites, ExecutorServiceConfigurator, ExecutorServiceFactory}
import com.typesafe.config.Config
import java.util.concurrent.{AbstractExecutorService, ExecutorService, ThreadFactory, TimeUnit}
import java.util.Collections
import javafx.application.{Application, Platform}
import javafx.embed.swing.JFXPanel


// First we wrap invokeLater/runLater as an ExecutorService
abstract class GUIExecutorService extends AbstractExecutorService {
  def execute(command: Runnable): Unit

  def shutdown(): Unit = ()

  def shutdownNow() = Collections.emptyList[Runnable]

  def isShutdown = false

  def isTerminated = false

  def awaitTermination(l: Long, timeUnit: TimeUnit) = true
}

object JavaFXExecutorService extends GUIExecutorService {
  override def execute(command: Runnable) = Platform.runLater(command)
}


