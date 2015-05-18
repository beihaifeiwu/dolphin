package com.freetmp.maven.mbg.extend.plugin;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Created by LiuPin on 2015/5/18.
 */
@ContextConfiguration(locations = {"classpath:xmbg-spring-config.xml"})
@TestExecutionListeners({DbUnitTestExecutionListener.class })
public class XmbgBaseTest extends AbstractTransactionalJUnit4SpringContextTests {
}
