package com.freetmp.dolphin.dpendency.manager.pom

import com.freetmp.dolphin.dependency.manager.artifact.*
import com.freetmp.dolphin.dependency.manager.config.Configuration
import org.assertj.core.api.Assertions.*
import org.junit.Test as test

/**
 * Created by LiuPin on 2015/6/10.
 */
public class MavenArtifactTest {

  fun String.normalize(): String = trim().replace("\\s+".toRegex(), " ")

  test fun testResolve() {
    val result = resolve("org.eclipse.aether:aether-util:1.0.0.v20140518", Configuration {
      localRepo = "target/lib"
    })

    assertThat(result.resolvedFiles).hasSize(2);
    val names = arrayListOf("aether-util-1.0.0.v20140518.jar", "aether-api-1.0.0.v20140518.jar")
    result.resolvedFiles.forEachIndexed { i, file -> assertThat(file).hasName(names[i]) }
  }

  test fun testResolveArtifact(){
    val result = resolveArtifact("org.eclipse.aether:aether-util:1.0.0.v20140518", Configuration { localRepo = "target/lib" })
    assertThat(result.getFile()).hasName("aether-util-1.0.0.v20140518.jar")
  }

  test fun testResolveTransitiveDependencies(){
    val result = resolveTransitiveDependencies("org.eclipse.aether:aether-util:1.0.0.v20140518",Configuration { localRepo = "target/lib" })
    assertThat(result).hasSize(2);
    val names = arrayListOf("aether-util-1.0.0.v20140518.jar", "aether-api-1.0.0.v20140518.jar")
    result.forEachIndexed { i, artifact -> assertThat(artifact.getFile()).hasName(names[i]) }
  }

  test fun testGetDependencyHierarchy(){
    val result = getDependencyHierarchy("org.apache.maven:maven-aether-provider:3.1.0",Configuration { localRepo = "target/lib" })
    assertThat(result.normalize()).isEqualTo(
"""
org.apache.maven:maven-aether-provider:jar:3.1.0
+-org.apache.maven:maven-model:jar:3.1.0 [compile]
| \-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile]
+-org.apache.maven:maven-model-builder:jar:3.1.0 [compile]
| +-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile]
| +-org.codehaus.plexus:plexus-interpolation:jar:1.16 [compile]
| +-org.codehaus.plexus:plexus-component-annotations:jar:1.5.5 [compile]
| \-org.apache.maven:maven-model:jar:3.1.0 [compile]
+-org.apache.maven:maven-repository-metadata:jar:3.1.0 [compile]
| \-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile]
+-org.eclipse.aether:aether-api:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-spi:jar:0.9.0.M2 [compile]
| \-org.eclipse.aether:aether-api:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-util:jar:0.9.0.M2 [compile]
| \-org.eclipse.aether:aether-api:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-impl:jar:0.9.0.M2 [compile]
| +-org.eclipse.aether:aether-api:jar:0.9.0.M2 [compile]
| +-org.eclipse.aether:aether-spi:jar:0.9.0.M2 [compile]
| \-org.eclipse.aether:aether-util:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-connector-wagon:jar:0.9.0.M2 [test]
| +-org.eclipse.aether:aether-api:jar:0.9.0.M2 [test]
| +-org.eclipse.aether:aether-spi:jar:0.9.0.M2 [test]
| +-org.eclipse.aether:aether-util:jar:0.9.0.M2 [test]
| \-org.apache.maven.wagon:wagon-provider-api:jar:2.4 [test] (version managed from 1.0)
|   \-org.codehaus.plexus:plexus-utils:jar:3.0.10 [test] (version managed from 3.0.8)
+-org.apache.maven.wagon:wagon-file:jar:2.4 [test]
| \-org.apache.maven.wagon:wagon-provider-api:jar:2.4 [test]
+-org.eclipse.sisu:org.eclipse.sisu.plexus:jar:0.0.0.M2a [compile]
| +-javax.enterprise:cdi-api:jar:1.0 [compile]
| | +-javax.annotation:jsr250-api:jar:1.0 [compile]
| | \-javax.inject:javax.inject:jar:1 [compile]
| +-com.google.guava:guava:jar:11.0.2 [compile] (version managed from 10.0.1)
| | \-com.google.code.findbugs:jsr305:jar:1.3.9 [compile]
| +-org.sonatype.sisu:sisu-guice:jar:no_aop:3.1.3 [compile] (version managed from 3.1.0)
| +-org.eclipse.sisu:org.eclipse.sisu.inject:jar:0.0.0.M2a [compile]
| | \-asm:asm:jar:3.3.1 [compile]
| +-org.codehaus.plexus:plexus-component-annotations:jar:1.5.5 [compile]
| +-org.codehaus.plexus:plexus-classworlds:jar:2.4.2 [compile] (version managed from 2.4)
| \-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile] (version managed from 2.1)
+-org.codehaus.plexus:plexus-component-annotations:jar:1.5.5 [compile]
+-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile]
+-org.sonatype.sisu:sisu-guice:jar:no_aop:3.1.3 [compile, optional]
| +-javax.inject:javax.inject:jar:1 [compile]
| \-com.google.guava:guava:jar:11.0.2 [compile] (version managed from 13.0.1)
\-junit:junit:jar:3.8.2 [test]
""".normalize())
  }

  test fun testGetDependencyTree(){
    val result = getDependencyTree("org.apache.maven:maven-aether-provider:3.1.0",Configuration { localRepo = "target/lib" })
    assertThat(result.normalize()).isEqualTo("""
org.apache.maven:maven-aether-provider:jar:3.1.0
+-org.apache.maven:maven-model:jar:3.1.0 [compile]
+-org.apache.maven:maven-model-builder:jar:3.1.0 [compile]
| \-org.codehaus.plexus:plexus-interpolation:jar:1.16 [compile]
+-org.apache.maven:maven-repository-metadata:jar:3.1.0 [compile]
+-org.eclipse.aether:aether-api:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-spi:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-util:jar:0.9.0.M2 [compile]
+-org.eclipse.aether:aether-impl:jar:0.9.0.M2 [compile]
+-org.eclipse.sisu:org.eclipse.sisu.plexus:jar:0.0.0.M2a [compile]
| +-javax.enterprise:cdi-api:jar:1.0 [compile]
| | \-javax.annotation:jsr250-api:jar:1.0 [compile]
| +-com.google.guava:guava:jar:11.0.2 [compile]
| | \-com.google.code.findbugs:jsr305:jar:1.3.9 [compile]
| +-org.eclipse.sisu:org.eclipse.sisu.inject:jar:0.0.0.M2a [compile]
| | \-asm:asm:jar:3.3.1 [compile]
| \-org.codehaus.plexus:plexus-classworlds:jar:2.4.2 [compile]
+-org.codehaus.plexus:plexus-component-annotations:jar:1.5.5 [compile]
+-org.codehaus.plexus:plexus-utils:jar:3.0.10 [compile]
\-org.sonatype.sisu:sisu-guice:jar:no_aop:3.1.3 [compile, optional]
  \-javax.inject:javax.inject:jar:1 [compile]
    """.normalize())
  }

  test fun testAvailableVersions(){
    val result = availableVersions("org.eclipse.aether","aether-api", Configuration { localRepo = "target/lib" })
    assertThat(result.getLowestVersion().toString()).isEqualToIgnoringCase("0.9.0.M1")
  }
}