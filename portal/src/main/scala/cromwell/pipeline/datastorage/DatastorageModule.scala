package cromwell.pipeline.datastorage

import com.typesafe.config.Config
import cromwell.pipeline.database.PipelineDatabaseEngine
import cromwell.pipeline.datastorage.dao.entry.{ ProjectEntry, UserEntry }
import cromwell.pipeline.datastorage.dao.repository.{ ProjectRepository, UserRepository }
import slick.jdbc.JdbcProfile

class DatastorageModule(config: Config) {
  lazy val pipelineDatabaseEngine: PipelineDatabaseEngine = new PipelineDatabaseEngine(config)
  lazy val profile: JdbcProfile = pipelineDatabaseEngine.profile
  lazy val databaseLayer: DatabaseLayer = new DatabaseLayer(profile)

  lazy val userRepository: UserRepository = new UserRepository(pipelineDatabaseEngine, databaseLayer)
  lazy val projectRepository: ProjectRepository = new ProjectRepository(pipelineDatabaseEngine, databaseLayer)
}

trait Profile {
  val profile: JdbcProfile
}

// https://books.underscore.io/essential-slick/essential-slick-3.html#scaling-to-larger-codebases
class DatabaseLayer(val profile: JdbcProfile) extends Profile with UserEntry with ProjectEntry
