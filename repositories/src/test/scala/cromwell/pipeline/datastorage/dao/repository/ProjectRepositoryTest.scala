package cromwell.pipeline.datastorage.dao.repository

import java.util.UUID

import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import com.typesafe.config.Config
import cromwell.pipeline.datastorage.dto.{ Project, ProjectId, User, UserId }
import cromwell.pipeline.utils.{ ApplicationConfig, StringUtils }
import cromwell.pipeline.datastorage.DatastorageModule
import cromwell.pipeline.datastorage.dao.repository.utils.TestContainersUtils
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, Matchers }

class ProjectRepositoryTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with ForAllTestContainer {

  override val container: PostgreSQLContainer = TestContainersUtils.getPostgreSQLContainer()
  container.start()
  implicit val config: Config = TestContainersUtils.getConfigForPgContainer(container)
  private val datastorageModule: DatastorageModule = new DatastorageModule(config, ApplicationConfig.load(config))

  override protected def beforeAll(): Unit =
    datastorageModule.pipelineDatabaseEngine.updateSchema()

  private val userPassword = "-Pa$$w0rd-"

  import datastorageModule.userRepository
  import datastorageModule.projectRepository

  "ProjectRepository" when {

    "getUserById" should {

      "find newly added project by id" in {
        val newUser = getDummyUser(userPassword)
        val newProject = getDummyProject(newUser.userId)

        val addUserFuture = userRepository.addUser(newUser)
        val result = for {
          _ <- addUserFuture
          _ <- projectRepository.addProject(newProject)
          getById <- projectRepository.getProjectById(newProject.projectId)
        } yield getById

        result.map(optProject => optProject shouldEqual Some(newProject))
      }
    }
  }

  private def getDummyUser(password: String = userPassword, passwordSalt: String = "salt"): User = {
    val uuid = UUID.randomUUID().toString
    val passwordHash = StringUtils.calculatePasswordHash(password, passwordSalt)
    User(
      userId = UserId(uuid),
      email = s"JohnDoe-$uuid@cromwell.com",
      passwordHash = passwordHash,
      passwordSalt = passwordSalt,
      firstName = "FirstName",
      lastName = "LastName",
      profilePicture = None
    )
  }

  private def getDummyProject(ownerId: UserId): Project = {
    val uuid = UUID.randomUUID().toString
    Project(
      projectId = ProjectId(uuid),
      ownerId = ownerId,
      name = s"project-$uuid",
      repository = s"repo-$uuid",
      active = true
    )
  }
}
