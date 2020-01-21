package cromwell.pipeline.service

import cromwell.pipeline.datastorage.DatastorageModule
import cromwell.pipeline.utils.UtilsModule

import scala.concurrent.ExecutionContext

class ServiceModule(datastorageModule: DatastorageModule, utilsModule: UtilsModule)(
  implicit executionContext: ExecutionContext
) {
  lazy val authService: AuthService = new AuthService(datastorageModule.userRepository, utilsModule.authUtils)
  lazy val userService: UserService = new UserService(datastorageModule.userRepository)
  lazy val projectService: ProjectService = new ProjectService(datastorageModule.projectRepository)
}
