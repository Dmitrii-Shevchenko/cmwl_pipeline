package cromwell.pipeline.utils

import cromwell.pipeline.AuthConfig
import cromwell.pipeline.utils.auth.{ AuthUtils, SecurityDirective }

class UtilsModule(authConfig: AuthConfig) {
  lazy val authUtils: AuthUtils = new AuthUtils(authConfig)
  lazy val securityDirective: SecurityDirective = new SecurityDirective(authConfig)
}
