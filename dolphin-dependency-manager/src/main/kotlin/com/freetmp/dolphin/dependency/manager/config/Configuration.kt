package com.freetmp.dolphin.dependency.manager.config

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