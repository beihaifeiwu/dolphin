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
public class UpdateTest extends XmbgBaseTest {
  @Test
  public void testUpdateByExampleSelective() {
    // update with all field non null
    User user = buildUser(2L);
    UserExample example = new UserExample();
    example.createCriteria().andIdEqualTo(user.getId());

    int rows = mapper.updateByExampleSelective(user, example);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByExampleSelective(user, example);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate", "roles");
  }

  @Test
  public void testUpdateByExample() {
    // update with all field non null
    User user = buildUser(2L);
    UserExample example = new UserExample();
    example.createCriteria().andIdEqualTo(user.getId());

    int rows = mapper.updateByExample(user, example);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByExample(user, example);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");
  }

  @Test
  public void testUpdateByPrimaryKeySelective() {
    // update with all field non null
    User user = buildUser(2L);

    int rows = mapper.updateByPrimaryKeySelective(user);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByPrimaryKeySelective(user);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate", "roles");
  }

  @Test
  public void testUpdateByPrimaryKey() {
    // update with all field non null
    User user = buildUser(2L);

    int rows = mapper.updateByPrimaryKey(user);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByPrimaryKey(user);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");
  }

  @Test
  public void testBatchUpdate() {
    // update with all field non null
    List<User> list = new ArrayList<>();
    list.add(buildUser(1L));
    list.add(buildUser(2L));

    int rows = mapper.batchUpdate(list);

    assertThat(rows).isGreaterThan(0);

    for (User user : list) {
      User loaded = mapper.selectByPrimaryKey(user.getId());
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user, "registerDate");
    }

    // update with roles null
    for (User user : list) {
      user.setRoles(null);
    }

    rows = mapper.batchUpdate(list);

    assertThat(rows).isGreaterThan(0);

    for (User user : list) {
      User loaded = mapper.selectByPrimaryKey(user.getId());
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user, "registerDate", "roles");
    }

  }
}
