package com.freetmp.dolphin.dependency.manager.artifact

import com.freetmp.dolphin.dependency.manager.config.Configuration
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.deployment.DeployRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.graph.DependencyNode
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.installation.InstallRequest
import org.eclipse.aether.repository.Authentication
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator
import org.eclipse.aether.util.repository.AuthenticationBuilder
import java.io.File

/**
 * Created by LiuPin on 2015/6/9.
 */
fun newRepositorySystem(): RepositorySystem {
  val locator: DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator()
  locator.addService(javaClass<RepositoryConnectorFactory>(), javaClass<BasicRepositoryConnectorFactory>())
  locator.addService(javaClass<TransporterFactory>(), javaClass<FileTransporterFactory>())
  locator.addService(javaClass<TransporterFactory>(), javaClass<HttpTransporterFactory>())

  locator.setErrorHandler(object : DefaultServiceLocator.ErrorHandler() {
    override fun serviceCreationFailed(type: Class<*>?, impl: Class<*>?, exception: Throwable?) {
      exception?.printStackTrace()
    }
  })

  return locator.getService(javaClass<RepositorySystem>())
}

fun newRepositorySystemSession(system: RepositorySystem, config: Configuration): DefaultRepositorySystemSession {
  val session = MavenRepositorySystemUtils.newSession()
  val localRepo = LocalRepository(config.localRepo)
  session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo))

  session.setTransferListener(ConsoleTransferListener())
  session.setRepositoryListener(ConsoleRepositoryListener())
  return session
}

fun newRepositories(vararg repos: String): MutableList<RemoteRepository> {
  return arrayListOf(
      remoteRepository("oschina", "default", "http://maven.oschina.net/content/groups/public/"),
      remoteRepository("central", "default", "http://central.maven.org/maven2/")
  )
}

fun remoteRepository(id: String, type: String, url: String, auth: Authentication? = null): RemoteRepository {
  val builder = RemoteRepository.Builder(id, type, url)
  if (auth != null) builder.setAuthentication(auth)
  return builder.build()
}

fun displayTree(node: DependencyNode) = node.accept(ConsoleDependencyGraphDumper())


data class ResolveResult(val root: DependencyNode, val resolvedFiles: List<File>, val resolvedClassPath: String)

fun resolve(groupId: String, artifactId: String, version: String, config: Configuration): ResolveResult {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system, config)
  val dependency = Dependency(DefaultArtifact(groupId, artifactId, "", "jar", version), "runtime")

  val collectRequest = CollectRequest()
  collectRequest.setRoot(dependency)
  config.remoteRepos.forEachIndexed { i, s -> collectRequest.addRepository(remoteRepository("id_$i", "default", s)) }

  val dependencyRequest = DependencyRequest()
  dependencyRequest.setCollectRequest(collectRequest)

  val rootNode = system.resolveDependencies(session, dependencyRequest).getRoot()
  displayTree(rootNode)

  val nlg = PreorderNodeListGenerator()
  rootNode.accept(nlg)

  return ResolveResult(rootNode, nlg.getFiles(), nlg.getClassPath())
}

fun install(artifact: Artifact, pom: Artifact, config: Configuration) {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system, config)
  val installRequest: InstallRequest = InstallRequest()
  installRequest.addArtifact(artifact).addArtifact(pom)
  system.install(session, installRequest)
}

fun deploy(artifact: Artifact, pom: Artifact, config: Configuration) {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system, config)
  val auth = AuthenticationBuilder().addUsername(config.username).addPassword(config.password).build()
  val nexus = remoteRepository("deploy_target", "default", config.deployRepos, auth)

  val deployRequest = DeployRequest()
  deployRequest.addArtifact(artifact).addArtifact(pom)
  deployRequest.setRepository(nexus)

  system.deploy(session, deployRequest)
}