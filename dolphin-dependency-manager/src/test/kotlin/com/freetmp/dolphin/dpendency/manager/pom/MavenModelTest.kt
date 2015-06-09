package com.freetmp.dolphin.dpendency.manager.pom

import com.freetmp.dolphin.dependency.manager.pom.RepositoryModelResolver
import org.apache.maven.model.building.DefaultModelBuilder
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.building.ModelBuildingRequest
import java.io.File
import org.junit.Test as test

/**
 * Created by LiuPin on 2015/6/8.
 */
public class MavenModelTest {

  val baseDir = "C:\\Users\\Administrator\\git\\dolphin\\dolphin-dependency-manager\\target"
  val repository = "http://maven.oschina.net/content/groups/public/"

  test fun testModuleBuild() {
    val mbr: ModelBuildingRequest = DefaultModelBuildingRequest()
    mbr.setProcessPlugins(false)
    val url = MavenModelTest::class.javaClass.getClassLoader().getResource("spring-boot-starter-test-1.2.4.RELEASE.pom.xml")
    mbr.setPomFile(File(url.getPath()))
    mbr.setModelResolver(RepositoryModelResolver(File(baseDir), repository))
    mbr.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL)
    var model = DefaultModelBuilderFactory().newInstance().build(mbr).getEffectiveModel()
    println(model)
  }
}