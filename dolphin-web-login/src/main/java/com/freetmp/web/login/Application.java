package com.freetmp.web.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by LiuPin on 2015/5/14.
 */
@SpringBootApplication
public class Application  extends WebMvcConfigurerAdapter {

  @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/css/**","/fonts/**","/images/**","/js/**")
        .addResourceLocations("classpath:/css/")
        .addResourceLocations("classpath:/fonts/")
        .addResourceLocations("classpath:/images/")
        .addResourceLocations("classpath:/js/");
  }

  public static void main(String... args){
    SpringApplication.run(Application.class, args);
  }
}
