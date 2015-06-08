package com.freetmp.dolphin.dependency.manager.pom

import org.apache.maven.model.Parent
import org.apache.maven.model.Repository
import org.apache.maven.model.building.ModelSource2
import org.apache.maven.model.resolution.ModelResolver
import java.io.File

/**
 * Created by LiuPin on 2015/6/8.
 */
public class RepositoryModelResolver(val repository: File, val mavenRepository: String) : ModelResolver {

  override fun newCopy(): ModelResolver? {
    throw UnsupportedOperationException()
  }

  override fun resolveModel(groupId: String?, artifactId: String?, version: String?): ModelSource2? {
    throw UnsupportedOperationException()
  }

  override fun resolveModel(parent: Parent?): ModelSource2? {
    throw UnsupportedOperationException()
  }

  override fun addRepository(repository: Repository?) {
    throw UnsupportedOperationException()
  }

  override fun addRepository(repository: Repository?, replace: Boolean) {
    throw UnsupportedOperationException()
  }
}