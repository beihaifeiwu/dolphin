package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.mbg.constant.DatabaseType;
import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.mapper.UserMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by LiuPin on 2015/5/18.
 */
@ContextConfiguration(locations = {"classpath:xmbg-spring-config.xml"})
@TestExecutionListeners({DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:datasets/User.xml"})
@DatabaseTearDown(type = DatabaseOperation.CLEAN_INSERT, value = {"classpath:datasets/User.xml"})
public abstract class XmbgBaseTest extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired UserMapper mapper;

  @Value("${case.sensitive}") boolean caseSensitive;

  @Value("${escape.pattern}") String escapePattern;

  @Value("${jdbc.url}") String jdbcUrl;

  protected void validate(List<User> list, List<User> loadeds) {
    for (int i = 0; i < list.size(); i++) {
      User user = list.get(i);
      User loaded = loadeds.get(i);
      assertThat(loaded).isNotNull();
      assertThat(loaded.getRoles()).isNotNull();
      assertThat(loaded).isEqualToIgnoringGivenFields(user, "id", "registerDate");
    }
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

  public String escapeOrNot(String column){
    return caseSensitive ? escapePattern.replace("?", column) : column;
  }

  public DatabaseType dbType(){
    String[] arrays = jdbcUrl.split(":");
    String dbType;
    if(equalsIgnoreCase(arrays[1], "log4jdbc")){
      dbType = upperCase(arrays[2]);
    }else {
      dbType = upperCase(arrays[1]);
    }
    return DatabaseType.valueOf(dbType);
  }

  public boolean isUnsupported(DatabaseType... types){
    DatabaseType type = dbType();
    for (DatabaseType dt : types){
      if(type.equals(dt)) return true;
    }
    return false;
  }
}
