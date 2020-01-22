package cromwell.pipeline.datastorage.dao.repository

import java.util.UUID

import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import com.typesafe.config.Config
import cromwell.pipeline.datastorage.DatastorageModule
import cromwell.pipeline.datastorage.dao.repository.utils.TestContainersUtils
import cromwell.pipeline.datastorage.dto.{ User, UserId }
import cromwell.pipeline.datastorage.dto.User.UserEmail
import cromwell.pipeline.utils.{ ApplicationConfig, StringUtils }
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, Matchers }

class UserRepositoryTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with ForAllTestContainer {

  override val container: PostgreSQLContainer = TestContainersUtils.getPostgreSQLContainer()
  container.start()
  implicit val config: Config = TestContainersUtils.getConfigForPgContainer(container)
  private val datastorageModule: DatastorageModule = new DatastorageModule(config, ApplicationConfig.load(config))

  override protected def beforeAll(): Unit =
    datastorageModule.pipelineDatabaseEngine.updateSchema()

  import datastorageModule.userRepository

  "UserRepository" when {

    "getUserById" should {

      "find newly added user by id" in {
        val dummyUser: User = getDummyUser()
        val addUserFuture = userRepository.addUser(dummyUser)
        val result = for {
          _ <- addUserFuture
          getById <- userRepository.getUserById(dummyUser.userId)
        } yield getById

        result.map(optUser => optUser shouldEqual Some(dummyUser))
      }
    }

    "getUserByEmail" should {

      "find newly added user by email" in {
        val dummyUser: User = getDummyUser()

        val addUserFuture = userRepository.addUser(dummyUser)
        val result = for {
          _ <- addUserFuture
          getByEmail <- userRepository.getUserByEmail(dummyUser.email)
        } yield getByEmail

        result.map(optUser => optUser shouldEqual Some(dummyUser))
      }
    }

    "getUsersByEmail" should {

      "should find newly added user by email pattern" taggedAs (Dao) in {
        val newUser: User = getDummyUser()
        userRepository
          .addUser(newUser)
          .flatMap(
            _ =>
              userRepository
                .getUsersByEmail(newUser.email)
                .map(repoResp => repoResp should contain theSameElementsAs Seq(newUser))
          )
      }
    }

    "updateUser" should {
      "update email, firstName and lastName" in {
        val dummyUser: User = getDummyUser()
        userRepository.addUser(dummyUser)

        val updatedUser =
          dummyUser.copy(email = "updated@email.com", firstName = "updatedFName", lastName = "updatedLName")
        userRepository
          .updateUser(updatedUser)
          .flatMap(
            _ => userRepository.getUserById(dummyUser.userId).map(dummyUser => dummyUser.get shouldEqual updatedUser)
          )
      }
    }

    "updatePassword" should {
      "update password" in {
        val dummyUser: User = getDummyUser()
        userRepository.addUser(dummyUser)

        val updatedUser = dummyUser.copy(passwordHash = getDummyUser("newPassword").passwordHash)
        userRepository
          .updatePassword(updatedUser)
          .flatMap(
            _ => userRepository.getUserById(dummyUser.userId).map(dummyUser => dummyUser.get shouldEqual updatedUser)
          )
      }
    }
  }

  private def getDummyUser(
    uuid: String = UUID.randomUUID().toString,
    email: UserEmail = "JohnDoe-@cromwell.com",
    password: String = "-Pa$$w0rd-",
    passwordSalt: String = "salt",
    firstName: String = "FirstName",
    lastName: String = "Lastname",
    active: Boolean = true
  ): User = {
    val passwordHash = StringUtils.calculatePasswordHash(password, passwordSalt)
    User(
      UserId(uuid),
      s"JohnDoe-$uuid@cromwell.com",
      passwordHash,
      passwordSalt,
      firstName,
      lastName,
      None,
      active
    )
  }

}
