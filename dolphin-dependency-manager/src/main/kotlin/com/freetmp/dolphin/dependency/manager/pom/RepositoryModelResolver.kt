package com.freetmp.dolphin.dependency.manager.pom

import com.freetmp.dolphin.dependency.manager.config.Configuration
import com.freetmp.dolphin.dependency.manager.config.fillMavenModelReposTo
import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.FileModelSource
import org.apache.maven.model.building.ModelSource2
import org.apache.maven.model.resolution.ModelResolver
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by LiuPin on 2015/6/8.
 */
public class RepositoryModelResolver(val config: Configuration) : ModelResolver {

  val repositories = arrayListOf<Repository>()
  val localRepo = File(config.localRepo)

  init {
    config.fillMavenModelReposTo { repositories.add(it) }
  }

  fun getLocalFile(groupId: String, artifactId: String, versionId: String): File {
    var pom: File = localRepo
    val groupIdTokens = groupId.split("\\.".toRegex()).toTypedArray()
    listOf(*groupIdTokens, artifactId, versionId, "$artifactId-$versionId.pom").forEach { pom = File(pom, it) }
    return pom
  }

  fun download(pom: File) {
    for (repository in repositories) {
      var urlStr = repository.getUrl()
      urlStr = if (urlStr.endsWith("/")) urlStr.substring(0, urlStr.length() - 1) else urlStr
      val url = URL(urlStr + pom.getAbsolutePath().substring(this.localRepo.getAbsolutePath().length()).replace("\\", "/"))

      println("Downloading $url")

      try {
        val conn = url.openConnection() as HttpURLConnection
        conn.setInstanceFollowRedirects(true)
        pom.getParentFile().mkdirs()
        conn.getInputStream().use { input -> pom.outputStream().use { output -> input.copyTo(output) } }
        return
      } catch(e: Exception) {
        println("Failed to download $url")
      }

    }
    throw IOException("Failed to download $pom")
  }

  override fun newCopy(): ModelResolver? = RepositoryModelResolver(config)

  override fun resolveModel(groupId: String?, artifactId: String?, version: String?): ModelSource2? {
    var pom = getLocalFile(groupId!!, artifactId!!, version!!)
    if (!pom.exists()) download(pom)
    return FileModelSource(pom)
  }

  override fun resolveModel(parent: Parent?): ModelSource2? {
    return resolveModel(parent!!.getGroupId(), parent.getArtifactId(), parent.getVersion())
  }

  override fun addRepository(repository: Repository?) {
    if (repositories.any { it.getId() == repository!!.getId() }) else repositories.add(repository)
  }

  override fun addRepository(repository: Repository?, replace: Boolean) {

    var index = repositories.indexOfFirst { it.getId() == repository!!.getId() }
    if (index == -1) {
      repositories.add(repository)
    } else {
      repositories.remove(index)
      if (index == repositories.size()) repositories.add(repository) else repositories.add(index, repository)
    }

  }
}