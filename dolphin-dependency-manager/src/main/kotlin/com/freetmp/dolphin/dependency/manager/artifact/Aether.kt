package com.freetmp.dolphin.dependency.manager.artifact

import com.freetmp.dolphin.dependency.manager.config.Configuration
import com.freetmp.dolphin.dependency.manager.config.fillDeployRepoTo
import com.freetmp.dolphin.dependency.manager.config.fillReposTo
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
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
import org.eclipse.aether.resolution.*
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.artifact.JavaScopes
import org.eclipse.aether.util.filter.DependencyFilterUtils
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils
import org.eclipse.aether.util.graph.transformer.ConflictResolver
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator
import org.eclipse.aether.util.repository.AuthenticationBuilder
import org.eclipse.aether.version.Version
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

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

fun displayTree(node: DependencyNode) = node.accept(ConsoleDependencyGraphDumper())

data class ResolveResult(val root: DependencyNode, val resolvedFiles: List<File>, val resolvedClassPath: String)

inline fun <T> template(full: String, config: Configuration, run: (RepositorySystem, RepositorySystemSession, Artifact) -> T): T {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system, config)
  val artifact = DefaultArtifact(full)

  return run(system, session, artifact)
}

inline fun template(config: Configuration, run: (RepositorySystem, RepositorySystemSession) -> Unit) {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system, config)
  run(system, session)
}

fun resolve(full: String, config: Configuration): ResolveResult {
  return template<ResolveResult>(full, config) { system, session, artifact ->
    val dependency = Dependency(artifact, JavaScopes.RUNTIME)

    val collectRequest = CollectRequest()
    collectRequest.setRoot(dependency)
    config.fillReposTo { collectRequest.addRepository(it) }

    val dependencyRequest = DependencyRequest()
    dependencyRequest.setCollectRequest(collectRequest)
    val rootNode = system.resolveDependencies(session, dependencyRequest).getRoot()
    val nlg = PreorderNodeListGenerator()
    rootNode.accept(nlg)

    return ResolveResult(rootNode, nlg.getFiles(), nlg.getClassPath())
  }
}

fun resolveArtifact(full: String, config: Configuration): Artifact {
  return template(full, config) { system, session, artifact ->
    val request = ArtifactRequest()
    request.setArtifact(artifact)
    config.fillReposTo { request.addRepository(it) }

    val artifactResult = system.resolveArtifact(session, request)
    artifactResult.getArtifact()
  }

}

fun resolveTransitiveDependencies(full: String, config: Configuration): List<Artifact> {
  return template(full, config) { system, session, artifact ->
    val filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE)
    val collectRequest = CollectRequest()
    collectRequest.setRoot(Dependency(artifact, JavaScopes.COMPILE))
    config.fillReposTo { collectRequest.addRepository(it) }
    val dependencyRequest = DependencyRequest(collectRequest, filter)
    system.resolveDependencies(session, dependencyRequest).getArtifactResults().map { it.getArtifact() }
  }
}

fun getDependencyHierarchy(full: String, config: Configuration): String {
  return template(full, config) { system, session, artifact ->
    (session as DefaultRepositorySystemSession).setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true)
    session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true)

    val adr = ArtifactDescriptorRequest()
    adr.setArtifact(artifact)
    config.fillReposTo { adr.addRepository(it) }
    val adResult = system.readArtifactDescriptor(session, adr)

    val collectRequest = CollectRequest()
    collectRequest.setRootArtifact(adResult.getArtifact()).setDependencies(adResult.getDependencies())
        .setManagedDependencies(adResult.getManagedDependencies())
        .setRepositories(adResult.getRepositories())

    val collectResult = system.collectDependencies(session, collectRequest)

    val out = ByteArrayOutputStream()
    collectResult.getRoot().accept(ConsoleDependencyGraphDumper(PrintStream(out)))
    out.toString()
  }
}

fun getDependencyTree(full: String, config: Configuration): String {
  return template(full,config){ system, session, artifact ->
    val collectRequest = CollectRequest()
    collectRequest.setRoot(Dependency(artifact,""))
    config.fillReposTo { collectRequest.addRepository(it) }
    val collectResult = system.collectDependencies(session, collectRequest)
    val out = ByteArrayOutputStream()
    collectResult.getRoot().accept(ConsoleDependencyGraphDumper(PrintStream(out)))
    out.toString()
  }
}

fun install(artifact: Artifact, pom: Artifact, config: Configuration) {
  template(config) { system, session ->
    val installRequest: InstallRequest = InstallRequest()
    installRequest.addArtifact(artifact).addArtifact(pom)
    system.install(session, installRequest)
  }

}

fun deploy(artifact: Artifact, pom: Artifact, config: Configuration) {
  template(config) { system, session ->
    val deployRequest = DeployRequest()
    deployRequest.addArtifact(artifact).addArtifact(pom)
    config.fillDeployRepoTo { deployRequest.setRepository(it) }
    system.deploy(session, deployRequest)
  }
}

fun availableVersions(groupId: String, artifactId: String, config: Configuration): VersionRangeResult {
  return template("$groupId:$artifactId:[0,)", config) { system, session, artifact ->
    val vrr = VersionRangeRequest()
    vrr.setArtifact(artifact)
    config.fillReposTo { vrr.addRepository(it) }
    system.resolveVersionRange(session, vrr)
  }
}

