import java.util.concurrent.{ExecutorService, ThreadFactory}

import akka.dispatch.{DispatcherPrerequisites, ExecutorServiceConfigurator, ExecutorServiceFactory}
import com.typesafe.config.Config

/**
  * Created by n.werneck on 17/06/16.
  */
// Then we create an ExecutorServiceConfigurator so that Akka can use our JavaFXExecutorService for the dispatchers
class JavaFXEventThreadExecutorServiceConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends ExecutorServiceConfigurator(config, prerequisites) {
  private val f = new ExecutorServiceFactory {
    def createExecutorService: ExecutorService = JavaFXExecutorService
  }

  def createExecutorServiceFactory(id: String, threadFactory: ThreadFactory): ExecutorServiceFactory = f

}
