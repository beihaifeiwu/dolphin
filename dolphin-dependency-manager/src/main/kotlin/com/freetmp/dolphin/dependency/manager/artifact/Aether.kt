package com.freetmp.dolphin.dependency.manager.artifact

import com.freetmp.dolphin.dependency.manager.config.Configuration
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory

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

fun newRepositorySystemSession(system: RepositorySystem): DefaultRepositorySystemSession {
  val session = MavenRepositorySystemUtils.newSession()
  val  localRepo = LocalRepository(Configuration.localRepo)
  session.setLocalRepositoryManager(system.newLocalRepositoryManager(session,localRepo))

  session.setTransferListener()
  return session
}

