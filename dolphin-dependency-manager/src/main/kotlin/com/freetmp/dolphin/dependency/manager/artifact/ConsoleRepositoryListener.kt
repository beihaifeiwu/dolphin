package com.freetmp.dolphin.dependency.manager.artifact

import org.eclipse.aether.AbstractRepositoryListener
import org.eclipse.aether.RepositoryEvent
import java.io.PrintStream

/**
 * Created by LiuPin on 2015/6/10.
 */
public class ConsoleRepositoryListener(val out: PrintStream = System.out) : AbstractRepositoryListener() {
  override fun artifactDeployed(event: RepositoryEvent?) {
    out.println("Deployed ${event!!.getArtifact()} to ${event.getRepository()}")
  }

  override fun artifactDeploying(event: RepositoryEvent?) {
    out.println("Deploying ${event!!.getArtifact()} to ${event.getRepository()}")
  }

  override fun artifactDescriptorInvalid(event: RepositoryEvent?) {
    out.println("Invalid artifact descriptor for ${event!!.getArtifact()}: ${event.getException().getMessage()}")
  }

  override fun artifactDescriptorMissing(event: RepositoryEvent?) {
    out.println("Missing artifact descriptor for ${event!!.getArtifact()}")
  }

  override fun artifactInstalled(event: RepositoryEvent?) {
    out.println("Installed ${event!!.getArtifact()} to ${event.getFile()}")
  }

  override fun artifactInstalling(event: RepositoryEvent?) {
    out.println("Installing ${event!!.getArtifact()} to ${event.getFile()}")
  }

  override fun artifactDownloading(event: RepositoryEvent?) {
    out.println("Downloading artifact ${event!!.getArtifact()} from ${event.getRepository()}")
  }

  override fun artifactResolved(event: RepositoryEvent?) {
    out.println("Resolved artifact ${event!!.getArtifact()} from ${event.getRepository()}")
  }

  override fun artifactDownloaded(event: RepositoryEvent?) {
    out.println("Downloaded artifact ${event!!.getArtifact()} from ${event.getRepository()}")
  }

  override fun artifactResolving(event: RepositoryEvent?) {
    out.println("Resolving artifact ${event!!.getArtifact()}")
  }

  override fun metadataResolving(event: RepositoryEvent?) {
    out.println("Resolving metadata ${event!!.getMetadata()}")
  }

  override fun metadataInstalled(event: RepositoryEvent?) {
    out.println("Installed metadata ${event!!.getMetadata()} to ${event.getFile()}")
  }

  override fun metadataDownloaded(event: RepositoryEvent?) {
    out.println("Downloaded metadata ${event!!.getMetadata()} from ${event.getRepository()}")
  }

  override fun metadataDownloading(event: RepositoryEvent?) {
    out.println("Downloading metadata ${event!!.getMetadata()} from ${event.getRepository()}")
  }

  override fun metadataInstalling(event: RepositoryEvent?) {
    out.println("Installing metadata ${event!!.getMetadata()} to ${event.getFile()}")
  }

  override fun metadataInvalid(event: RepositoryEvent?) {
    out.println("Invalid metadata ${event!!.getMetadata()}")
  }

  override fun metadataDeployed(event: RepositoryEvent?) {
    out.println("Deployed metadata ${event!!.getMetadata()} to ${event.getRepository()}")
  }

  override fun metadataResolved(event: RepositoryEvent?) {
    out.println("Resolved metadata ${event!!.getMetadata()} from ${event.getRepository()}")
  }

  override fun metadataDeploying(event: RepositoryEvent?) {
    out.println("Resolving metadata ${event!!.getMetadata()} from ${event.getRepository()}")
  }
}