//package cromwell.pipeline.datastorage.dao.repository
//
//import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
//import com.typesafe.config.Config
//import cromwell.pipeline.datastorage.DatastorageModule
//import cromwell.pipeline.datastorage.dao.repository.utils.TestUserUtils
//import cromwell.pipeline.datastorage.dto.{ User, UserId }
//import cromwell.pipeline.utils.{ ApplicationConfig, StringUtils, TestContainersUtils }
//import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, Matchers }
//
//import scala.concurrent.Future
//
//class UserRepositoryTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with ForAllTestContainer {
//
//  override val container: PostgreSQLContainer = TestContainersUtils.getPostgreSQLContainer()
//
//  var userRepository: UserRepository = _
//
//  override protected def beforeAll(): Unit = {
//    container.start()
//    implicit val config: Config = TestContainersUtils.getConfigForPgContainer(container)
//    val datastorageModule: DatastorageModule = new DatastorageModule(ApplicationConfig.load(config))
//    userRepository = datastorageModule.userRepository
//    datastorageModule.pipelineDatabaseEngine.updateSchema()
//  }
//
//  override protected def afterAll(): Unit =
//    container.stop()
//
//  private val newPasswordHash: String = StringUtils.calculatePasswordHash("newPassword", "salt")
//
//  "UserRepository" when {
//
//    "getUserById" should {
//
//      "find newly added user by id" taggedAs (Dao) in {
//        val dummyUser = TestUserUtils.getDummyUser()
//        val result = for {
//          _ <- userRepository.addUser(dummyUser)
//          getById <- userRepository.getUserById(dummyUser.userId)
//        } yield getById
//
//        result.map(optUser => optUser shouldEqual Some(dummyUser))
//      }
//    }
//
//    "getUserByEmail" should {
//
//      "find newly added user by email" taggedAs (Dao) in {
//        val dummyUser = TestUserUtils.getDummyUser()
//        val result = for {
//          _ <- userRepository.addUser(dummyUser)
//          getByEmail <- userRepository.getUserByEmail(dummyUser.email)
//        } yield getByEmail
//
//        result.map(optUser => optUser shouldEqual Some(dummyUser))
//      }
//    }
//
//    "getUsersByEmail" should {
//
//      "should find newly added user by email pattern" taggedAs (Dao) in {
//        val dummyUser = TestUserUtils.getDummyUser()
//        userRepository
//          .addUser(dummyUser)
//          .flatMap(
//            _ =>
//              userRepository
//                .getUsersByEmail(dummyUser.email)
//                .map(repoResp => repoResp should contain theSameElementsAs Seq(dummyUser))
//          )
//      }
//    }
//
//    "updateUser" should {
//
//      "update email, firstName and lastName" taggedAs (Dao) in {
//        val dummyUser = TestUserUtils.getDummyUser()
//        userRepository.addUser(dummyUser)
//        val updatedUser =
//          dummyUser.copy(email = "updated@email.com", firstName = "updatedFName", lastName = "updatedLName")
//        userRepository
//          .updateUser(updatedUser)
//          .flatMap(
//            _ => userRepository.getUserById(dummyUser.userId).map(dummyUser => dummyUser.get shouldEqual updatedUser)
//          )
//      }
//    }
//
//    "updatePassword" should {
//
//      "update password" taggedAs (Dao) in {
//        val dummyUser = TestUserUtils.getDummyUser()
//        userRepository.addUser(dummyUser)
//        val updatedUser = dummyUser.copy(passwordHash = newPasswordHash)
//        userRepository
//          .updatePassword(updatedUser)
//          .flatMap(
//            _ => userRepository.getUserById(dummyUser.userId).map(dummyUser => dummyUser.get shouldEqual updatedUser)
//          )
//      }
//    }
//
//  }
//
//}
