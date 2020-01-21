package cromwell.pipeline

import com.typesafe.config.{ Config, ConfigFactory }
import cromwell.pipeline.controller.ControllerModule
import cromwell.pipeline.datastorage.DatastorageModule
import cromwell.pipeline.service.ServiceModule
import cromwell.pipeline.utils.UtilsModule

import scala.concurrent.ExecutionContext

final class ApplicationComponents(
  implicit val config: Config = ConfigFactory.load(),
  val executionContext: ExecutionContext
) {
  lazy val applicationConfig: ApplicationConfig = ApplicationConfig.load()
  lazy val utilsModule: UtilsModule = new UtilsModule(applicationConfig.authConfig)
  lazy val datastorageModule: DatastorageModule = new DatastorageModule(config)
  lazy val serviceModule: ServiceModule = new ServiceModule(datastorageModule, utilsModule)
  lazy val controllerModule: ControllerModule = new ControllerModule(serviceModule)
}
