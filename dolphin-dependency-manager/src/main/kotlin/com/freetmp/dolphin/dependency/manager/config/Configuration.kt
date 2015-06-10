package com.freetmp.dolphin.dependency.manager.config

import org.apache.maven.artifact.repository.Authentication
import org.apache.maven.model.Repository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.util.repository.AuthenticationBuilder
import kotlin.platform.platformName

/**
 * Created by LiuPin on 2015/6/9.
 */
public class Configuration(var localRepo: String = Configuration.localRepo,
                           var remoteRepos: MutableList<String> = arrayListOf(Configuration.remoteOSC, Configuration.remoteCentral),
                           var deployRepos: String = Configuration.remoteCentral,
                           var username: String = "admin",
                           var password: String = "password"
) {

  companion object Items {
    var localRepo = "target/local-repo"
    val remoteOSC = "http://maven.oschina.net/content/groups/public/"
    val remoteCentral = "http://central.maven.org/maven2/"
  }
}

inline fun Configuration(init: Configuration.() -> Unit): Configuration {
  val conf = Configuration()
  conf.init()
  return conf
}

inline fun Configuration.fillReposTo(add: (RemoteRepository) -> Unit) {
  remoteRepos.forEachIndexed { i, s ->
    add(RemoteRepository.Builder("id_$i", "default", s).build())
  }
}

inline fun Configuration.fillMavenModelReposTo(add: (Repository) -> Unit) {
  remoteRepos.forEachIndexed { i, s ->
    val mainRepo = Repository()
    mainRepo.setUrl(s)
    mainRepo.setId("id_$i")
    add(mainRepo)
  }
}

inline fun Configuration.fillDeployRepoTo(add: (RemoteRepository) -> Unit) {
  val builder = RemoteRepository.Builder("deploy_target", "default", deployRepos)
  val auth = AuthenticationBuilder().addUsername(username).addPassword(password).build()
  builder.setAuthentication(auth)
  add(builder.build())
}