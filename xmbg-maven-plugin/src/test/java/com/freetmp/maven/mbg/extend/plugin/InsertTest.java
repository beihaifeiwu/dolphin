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
public class InsertTest extends XmbgBaseTest {

  @Test
  public void testInsert() {
    // insert with normal field
    User user = buildUser(3L);

    int rows = mapper.insert(user);

    assertThat(rows).isEqualTo(1);

    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");

    // insert with null field
    user = buildUser(4L);
    user.setRoles(null);

    rows = mapper.insert(user);

    assertThat(rows).isEqualTo(1);

    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");
  }

  @Test
  public void testInsertSelective() {
    User user = buildUser(5L);

    user.setRoles(null);

    int rows = mapper.insertSelective(user);

    assertThat(rows).isEqualTo(1);

    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");
  }

  @Test
  public void testBatchInsert() {
    List<User> list = new ArrayList<>();
    list.add(buildUser(6L));
    list.add(buildUser(7L));

    int rows = mapper.batchInsert(list);

    assertThat(rows).isEqualTo(2);

    UserExample example = new UserExample();
    example.createCriteria().andIdGreaterThan(5L);

    example.setOrderByClause(escapeOrNot("id") + " asc");

    List<User> loadeds = mapper.selectByExample(example);

    validate(list, loadeds);
  }
}
