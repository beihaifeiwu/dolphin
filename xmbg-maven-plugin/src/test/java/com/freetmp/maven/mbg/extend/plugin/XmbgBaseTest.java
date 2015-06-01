package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.mapper.UserMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.Date;
import java.util.List;

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

  static {
    BasicConfigurator.configure();
    Logger.getRootLogger().setLevel(Level.INFO);
    Logger logger = LogManager.getLogger("org.dbunit");
    logger.setLevel(Level.ERROR);
    logger = LogManager.getLogger("org.springframework");
    logger.setLevel(Level.ERROR);
    logger = LogManager.getLogger("jdbc.audit");
    logger.setLevel(Level.ERROR);
    logger = LogManager.getLogger("jdbc.resultset");
    logger.setLevel(Level.ERROR);
    logger = LogManager.getLogger("jdbc.connection");
    logger.setLevel(Level.ERROR);
    logger = LogManager.getLogger("jdbc.sqlonly");
    logger.setLevel(Level.ERROR);
  }

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
}
