package cromwell.pipeline.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import com.typesafe.config.Config
import cromwell.pipeline.ApplicationComponents
import cromwell.pipeline.CromwellPipelineApp.log
import cromwell.pipeline.datastorage.dao.repository.UserRepository
import cromwell.pipeline.datastorage.dao.repository.utils.TestUserUtils
import cromwell.pipeline.datastorage.dto.user.{ PasswordUpdateRequest, UserUpdateRequest }
import cromwell.pipeline.datastorage.dto.{ User, UserNoCredentials }
import cromwell.pipeline.datastorage.utils.auth.AccessTokenContent
import cromwell.pipeline.utils.TestContainersUtils
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.{ AsyncWordSpec, BeforeAndAfter, BeforeAndAfterAll, Matchers }

class UserControllerItTest
    extends AsyncWordSpec
    with Matchers
    with ScalatestRouteTest
    with PlayJsonSupport
    with ForAllTestContainer
    with BeforeAndAfterAll
    with BeforeAndAfter {

  override val container: PostgreSQLContainer = TestContainersUtils.getPostgreSQLContainer()

  var userController: UserController = _
  var userRepository: UserRepository = _

  before {
    container.start()
    implicit val config: Config = TestContainersUtils.getConfigForPgContainer(container)
    val components: ApplicationComponents = new ApplicationComponents()
    userController = components.controllerModule.userController
    userRepository = components.datastorageModule.userRepository
    components.datastorageModule.pipelineDatabaseEngine.updateSchema()
  }

  override def afterAll(): Unit = {
    throw new RuntimeException("Abra")
    container.stop()
  }

  private val password: String = "-Pa$$w0rd-"

  "UserController" when {

//    "getUsersByEmail" should {
//
//      "should find newly added user by email pattern" in {
//        val dummyUser: User = TestUserUtils.getDummyUser()
//        val userByEmailRequest: String = dummyUser.email
//        val seqUser: Seq[User] = Seq(dummyUser)
//        userRepository.addUser(dummyUser).map { _ =>
//          val accessToken = AccessTokenContent(dummyUser.userId.value)
//          Get("/users?email=" + userByEmailRequest) ~> userController.route(accessToken) ~> check {
//            status shouldBe StatusCodes.OK
//            responseAs[Seq[User]] shouldEqual seqUser
//          }
//        }
//      }
//    }
//
//    "deactivateUserById" should {
//
//      "return user's entity with false value if user was successfully deactivated" in {
//        val dummyUser: User = TestUserUtils.getDummyUser()
//        val deactivatedUserResponse = UserNoCredentials.fromUser(dummyUser.copy(active = false))
//        userRepository.addUser(dummyUser).map { _ =>
//          val accessToken = AccessTokenContent(dummyUser.userId.value)
//          Delete("/users") ~> userController.route(accessToken) ~> check {
//            responseAs[UserNoCredentials] shouldBe deactivatedUserResponse
//            status shouldBe StatusCodes.OK
//          }
//        }
//      }
//    }
//
//    "updateUser" should {
//
//      "return status code NoContent if user was successfully updated" in {
//        val dummyUser: User = TestUserUtils.getDummyUser()
//        val request = UserUpdateRequest(dummyUser.email, dummyUser.firstName, dummyUser.lastName)
//        userRepository
//          .addUser(dummyUser)
//          .flatMap(
//            _ =>
//              userRepository.updateUser(dummyUser).map { _ =>
//                val accessToken = AccessTokenContent(dummyUser.userId.value)
//                Put("/users", request) ~> userController.route(accessToken) ~> check {
//                  status shouldBe StatusCodes.NoContent
//                }
//              }
//          )
//      }
//    }
//
//    "updatePassword" should {
//
//      "return status code NoContent if user's password was successfully updated" in {
//        val dummyUser: User = TestUserUtils.getDummyUser()
//        val request = PasswordUpdateRequest(password, "newPassword", "newPassword")
//        userRepository
//          .addUser(dummyUser)
//          .flatMap(
//            _ =>
//              userRepository.updatePassword(dummyUser).map { _ =>
//                val accessToken = AccessTokenContent(dummyUser.userId.value)
//                Put("/users", request) ~> userController.route(accessToken) ~> check {
//                  status shouldBe StatusCodes.NoContent
//                }
//              }
//          )
//      }
//    }
  }
}
