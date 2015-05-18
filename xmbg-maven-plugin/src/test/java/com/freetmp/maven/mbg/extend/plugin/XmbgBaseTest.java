package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.xmbg.test.mapper.UserMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Created by LiuPin on 2015/5/18.
 */
@ContextConfiguration(locations = {"classpath:xmbg-spring-config.xml"})
@TestExecutionListeners({DbUnitTestExecutionListener.class })
public class XmbgBaseTest extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired UserMapper mapper;

  @BeforeClass
  public static void setUp(){
    BasicConfigurator.configure();
    Logger.getRootLogger().setLevel(Level.INFO);
    Logger logger = LogManager.getLogger("org.dbunit");
    logger.setLevel(Level. ERROR);
    logger = LogManager.getLogger("druid.sql");
    logger.setLevel(Level.DEBUG);
  }
}
