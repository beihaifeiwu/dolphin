package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.UserExample;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by LiuPin on 2015/5/18.
 */

public class CountByExampleTest extends XmbgBaseTest {

  @Test
  public void testCountByExample() {
    UserExample userExample = new UserExample();
    userExample.createCriteria().andLoginNameEqualTo("admin");

    int count = mapper.countByExample(userExample);

    assertThat(count).isEqualTo(1);
  }

}
