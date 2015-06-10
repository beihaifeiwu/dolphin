package com.freetmp.dolphin.dpendency.manager.pom

import com.freetmp.dolphin.dependency.manager.artifact.resolve
import com.freetmp.dolphin.dependency.manager.config.Configuration
import org.assertj.core.api.Assertions.*
import org.junit.Test as test

/**
 * Created by LiuPin on 2015/6/10.
 */
public class MavenArtifactTest {

  test fun testResolve() {
    val result = resolve("org.eclipse.aether:aether-util:1.0.0.v20140518", Configuration {
      localRepo = "target/lib"
    })

    assertThat(result.resolvedFiles).hasSize(2);
    val names = arrayListOf("aether-util-1.0.0.v20140518.jar", "aether-api-1.0.0.v20140518.jar")
    result.resolvedFiles.forEachIndexed { i, file -> assertThat(file).hasName(names[i]) }
  }

}