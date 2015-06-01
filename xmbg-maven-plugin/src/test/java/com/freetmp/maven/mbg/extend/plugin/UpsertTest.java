package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pin on 2015/6/1.
 */
public class UpsertTest extends XmbgBaseTest {

  @Test
  public void testUpsert() {

    User user = buildUser(3L);
    // user is new
    int rows = mapper.upsert(user, new String[]{"id", "name"});
    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(3L);
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualToIgnoringGivenFields(user, "registerDate");

    // user has been in the db
    user.setRoles("admin");
    user.setLoginName("admin_test");

    rows = mapper.upsert(user, new String[]{"id", "name"});
    assertThat(rows).isGreaterThanOrEqualTo(1);

    loaded = mapper.selectByPrimaryKey(3L);
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualToIgnoringGivenFields(user, "registerDate");

  }
}
