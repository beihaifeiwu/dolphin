package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by LiuPin on 2015/5/18.
 */
@DatabaseSetup({"classpath:datasets/User.xml"})
@DatabaseTearDown(type = DatabaseOperation.CLEAN_INSERT, value = {"classpath:datasets/User.xml"})
public class XmbgUserTest extends XmbgBaseTest {

  @Test
  public void testCountByExample(){
    UserExample userExample = new UserExample();
    userExample.createCriteria().andLoginNameEqualTo("admin");

    int count = mapper.countByExample(userExample);

    assertThat(count).isEqualTo(1);
  }

  @Test
  public void testDeleteByExample(){
    UserExample userExample = new UserExample();
    userExample.createCriteria().andLoginNameEqualTo("user");

    int rows = mapper.deleteByExample(userExample);

    assertThat(rows).isEqualTo(1);

    List<User> list = mapper.selectByExample(userExample);

    assertThat(list).isEmpty();
  }

  @Test
  public void testDeleteByPrimaryKey(){
    int rows = mapper.deleteByPrimaryKey(1L);

    assertThat(rows).isEqualTo(1);

    User user = mapper.selectByPrimaryKey(1L);

    assertThat(user).isNull();
  }

  @Test
  public void testInsert(){
    // insert with normal field
    User user = buildUser(3L);

    int rows = mapper.insert(user);

    assertThat(rows).isEqualTo(1);

    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");

    // insert with null field
    user = buildUser(4L);
    user.setRoles(null);

    rows = mapper.insert(user);

    assertThat(rows).isEqualTo(1);

    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded, "registerDate");
  }

  @Test
  public void testInsertSelective(){
    User user = buildUser(3L);

    user.setRoles(null);

    int rows = mapper.insertSelective(user);

    assertThat(rows).isEqualTo(1);

    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");
  }

  @Test
  public void testSelectByExample(){
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
  public void testSelectByPrimaryKey(){
    User user = mapper.selectByPrimaryKey(1L);

    assertThat(user).isNotNull();
    assertThat(user.getLoginName()).isEqualTo("admin");
  }

  @Test
  public void testUpdateByExampleSelective(){
    // update with all field non null
    User user = buildUser(2L);
    UserExample example = new UserExample();
    example.createCriteria().andIdEqualTo(user.getId());

    int rows = mapper.updateByExampleSelective(user, example);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByExampleSelective(user, example);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate","roles");
  }

  @Test
  public void testUpdateByExample(){
    // update with all field non null
    User user = buildUser(2L);
    UserExample example = new UserExample();
    example.createCriteria().andIdEqualTo(user.getId());

    int rows = mapper.updateByExample(user,example);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByExample(user,example);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");
  }

  @Test
  public void testUpdateByPrimaryKeySelective(){
    // update with all field non null
    User user = buildUser(2L);

    int rows = mapper.updateByPrimaryKeySelective(user);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByPrimaryKeySelective(user);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate","roles");
  }

  @Test
  public void testUpdateByPrimaryKey(){
    // update with all field non null
    User user = buildUser(2L);

    int rows = mapper.updateByPrimaryKey(user);

    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");

    // update with roles null
    user.setRoles(null);

    rows = mapper.updateByPrimaryKey(user);

    assertThat(rows).isEqualTo(1);
    loaded = mapper.selectByPrimaryKey(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getRoles()).isNull();
    assertThat(user).isEqualToIgnoringGivenFields(loaded,"registerDate");
  }

  @Test
  public void testBatchUpdate(){
    // update with all field non null
    List<User> list = new ArrayList<>();
    list.add(buildUser(1L));
    list.add(buildUser(2L));

    int rows = mapper.batchUpdate(list);

    assertThat(rows).isGreaterThan(0);

    for(User user : list){
      User loaded = mapper.selectByPrimaryKey(user.getId());
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user, "registerDate");
    }

    // update with roles null
    for(User user : list){
      user.setRoles(null);
    }

    rows = mapper.batchUpdate(list);

    assertThat(rows).isGreaterThan(0);

    for(User user : list){
      User loaded = mapper.selectByPrimaryKey(user.getId());
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user,"registerDate","roles");
    }

  }

  @Test
  public void testBatchInsert(){
    List<User> list = new ArrayList<>();
    list.add(buildUser(3L));
    list.add(buildUser(4L));

    int rows = mapper.batchInsert(list);

    assertThat(rows).isEqualTo(2);

    UserExample example = new UserExample();

    example.createCriteria().andIdGreaterThan(2L);
    example.setOrderByClause("id asc");

    List<User> loadeds = mapper.selectByExample(example);

    for(int i = 0; i < list.size(); i++){
      User user = list.get(i);
      User loaded = loadeds.get(i);
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user,"id", "registerDate");
    }

  }

  @Test
  public void testUpsert(){

    User user = buildUser(3L);
    // user is new
    int rows = mapper.upsert(user,new String[]{"id","name"});
    assertThat(rows).isEqualTo(1);
    User loaded = mapper.selectByPrimaryKey(3L);
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualToIgnoringGivenFields(user,"registerDate");

    // user has been in the db
    user.setRoles("admin");
    user.setLoginName("admin_test");

    rows = mapper.upsert(user,new String[]{"id","name"});
    assertThat(rows).isEqualTo(1);

    loaded = mapper.selectByPrimaryKey(3L);
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualToIgnoringGivenFields(user,"registerDate");

  }

  @Test
  public void testBatchUpsert(){

  }

  public User buildUser(Long id) {
    User user = new User();
    user.setId(id);
    user.setLoginName("tester_" + id);
    user.setName("Tester_" + id);
    user.setPassword("691b14d79bf0fa2215f155235df5e670b64394cc");
    user.setSalt("7efbd59d9741d34f");
    user.setRoles("user");
    user.setRegisterDate(new Date());
    return user;
  }
}
