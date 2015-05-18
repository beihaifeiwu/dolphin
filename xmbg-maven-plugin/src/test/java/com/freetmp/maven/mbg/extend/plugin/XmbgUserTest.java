package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import com.freetmp.xmbg.test.mapper.UserMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by LiuPin on 2015/5/18.
 */
@DatabaseSetup({"classpath:datasets/User.xml"})
@DatabaseTearDown(type = DatabaseOperation.CLEAN_INSERT, value = {"classpath:datasets/User.xml"})
public class XmbgUserTest extends XmbgBaseTest {

  @Autowired UserMapper mapper;

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
}
