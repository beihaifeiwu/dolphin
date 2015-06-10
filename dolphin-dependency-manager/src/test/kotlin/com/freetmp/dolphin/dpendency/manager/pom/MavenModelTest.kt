package com.freetmp.dolphin.dpendency.manager.pom

import com.freetmp.dolphin.dependency.manager.config.Configuration
import com.freetmp.dolphin.dependency.manager.pom.RepositoryModelResolver
import org.apache.maven.model.building.DefaultModelBuilder
import org.apache.maven.model.building.DefaultModelBuilderFactory
import org.apache.maven.model.building.DefaultModelBuildingRequest
import org.apache.maven.model.building.ModelBuildingRequest
import org.assertj.core.api.Assertions.*
import java.io.File
import org.junit.Test as test

/**
 * Created by LiuPin on 2015/6/8.
 */
public class MavenModelTest {

  test fun testModuleBuild() {
    val mbr: ModelBuildingRequest = DefaultModelBuildingRequest()
    mbr.setProcessPlugins(false)
    val url = MavenModelTest::class.javaClass.getClassLoader().getResource("spring-boot-starter-test-1.2.4.RELEASE.pom.xml")
    mbr.setPomFile(File(url.getPath()))
    mbr.setModelResolver(RepositoryModelResolver(Configuration { localRepo = "target/lib" }))
    mbr.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL)
    var model = DefaultModelBuilderFactory().newInstance().build(mbr).getEffectiveModel()

    assertThat(model.getDependencies()).hasSize(6)
    val versions = arrayListOf("4.12", "1.10.19", "1.3", "1.3", "4.1.6.RELEASE", "4.1.6.RELEASE")
    model.getDependencies().forEachIndexed { i, it -> assertThat(it.getVersion()).isEqualToIgnoringCase(versions[i]) }
  }
}