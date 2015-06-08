package com.freetmp.dolphin.dpendency.manager.pom

import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.building.ModelBuildingRequest
import java.io.File
import org.junit.Test as test

/**
 * Created by LiuPin on 2015/6/8.
 */
public class MavenModuleTest {

  test fun testModuleBuild() {
    val mbr: ModelBuildingRequest = DefaultModelBuildingRequest()
    mbr.setProcessPlugins(false)
    val url = MavenModuleTest::class .javaClass.getClassLoader().getResource("spring-boot-starter-test-1.2.4.RELEASE.pom.xml")
    mbr.setPomFile(File(url.getPath()))
    mbr.setModelResolver(null)
  }
}