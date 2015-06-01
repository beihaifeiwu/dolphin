package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pin on 2015/6/1.
 */
public class DeleteTest extends XmbgBaseTest {

  @Test
  public void testDeleteByExample() {
    UserExample userExample = new UserExample();
    userExample.createCriteria().andLoginNameEqualTo("user");

    int rows = mapper.deleteByExample(userExample);

    assertThat(rows).isEqualTo(1);

    List<User> list = mapper.selectByExample(userExample);

    assertThat(list).isEmpty();
  }

  @Test
  public void testDeleteByPrimaryKey() {
    int rows = mapper.deleteByPrimaryKey(1L);

    assertThat(rows).isEqualTo(1);

    User user = mapper.selectByPrimaryKey(1L);

    assertThat(user).isNull();
  }
}
