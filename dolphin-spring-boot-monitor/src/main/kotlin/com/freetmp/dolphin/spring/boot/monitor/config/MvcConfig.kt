package com.freetmp.dolphin.spring.boot.monitor.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Created by LiuPin on 2015/7/1.
 */
Configuration
open public class MvcConfig : WebMvcConfigurerAdapter() {

  override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
    registry?.addResourceHandler("/assets/**")?.addResourceLocations("classpath:/assets/","classpath:/assets/imgs/")
    registry?.addResourceHandler("/","/index","/index.html")?.addResourceLocations("classpath:/static/index.html")
  }
}