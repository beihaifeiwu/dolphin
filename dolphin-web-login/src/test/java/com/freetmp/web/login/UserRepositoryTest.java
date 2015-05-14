package com.freetmp.web.login;

import com.freetmp.web.login.entity.User;
import com.freetmp.web.login.repository.UserRepository;
import com.freetmp.web.login.service.CryptService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by LiuPin on 2015/5/14.
 */
@SpringApplicationConfiguration(classes = UserRepositoryTest.RepositoryTestConfiguration.class)
public class UserRepositoryTest extends AbstractJUnit4SpringContextTests {

  @Configuration
  @ComponentScan("com.freetmp.web.login")
  @EnableAutoConfiguration(exclude = {ThymeleafAutoConfiguration.class, WebMvcAutoConfiguration.class,
      FlywayAutoConfiguration.class,EmbeddedServletContainerAutoConfiguration.class})
  public static class RepositoryTestConfiguration {
  }

  @Autowired CryptService passwordService;
  @Autowired UserRepository repository;

  @Test
  public void testInsert(){
    repository.save(User.builder().id(1L).email("admin@test.com").password(passwordService.md5Encrypt("admin", 1L)).build());
    repository.save(User.builder().id(2L).email("user@test.com").password(passwordService.md5Encrypt("user", 2L)).build());
  }
}
