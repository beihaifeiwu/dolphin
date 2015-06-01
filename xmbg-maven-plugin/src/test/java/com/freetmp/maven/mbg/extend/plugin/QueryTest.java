package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pin on 2015/6/1.
 */
public class QueryTest extends XmbgBaseTest {

  @Test
  public void testSelectByExample() {
    UserExample userExample = new UserExample();
    userExample.createCriteria().andLoginNameEqualTo("admin").andIdLessThan(10L);

    List<User> list = mapper.selectByExample(userExample);

    assertThat(list).hasSize(1);

    userExample = new UserExample();
    userExample.createCriteria().andIdLessThan(3L);

    list = mapper.selectByExample(userExample);

    assertThat(list).hasSize(2);
  }

  @Test
  public void testSelectByExampleWithPagination(){
    List<User> users = new ArrayList<>();
    for(long i = 3; i < 13; i++){
      users.add(buildUser(i));
    }
    int rows = mapper.batchInsert(users);
    assertThat(rows).isEqualTo(10);

    UserExample userExample = new UserExample();
    userExample.createCriteria().andRolesEqualTo("user");
    userExample.boundBuilder().limit(10).offset(1).build();

    List<User> selected = mapper.selectByExample(userExample);

    assertThat(selected).hasSize(10);
  }

  @Test
  public void testSelectByPrimaryKey() {
    User user = mapper.selectByPrimaryKey(1L);

    assertThat(user).isNotNull();
    assertThat(user.getLoginName()).isEqualTo("admin");
  }
}
