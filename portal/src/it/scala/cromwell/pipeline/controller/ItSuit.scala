package cromwell.pipeline.controller

import com.dimafeng.testcontainers.PostgreSQLContainer
import cromwell.pipeline.utils.TestContainersUtils
import org.scalatest.{ BeforeAndAfterAll, Suite }

class ItSuit extends Suite with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    val container: PostgreSQLContainer = TestContainersUtils.getPostgreSQLContainer()
    container.start()
  }
  override def afterAll(): Unit = {}
}
