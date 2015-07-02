package com.freetmp.dolphin.spring.boot.monitor;

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.platform.platformStatic

/**
 * Created by LiuPin on 2015/7/1.
 */
SpringBootApplication
open public class Application {
  companion object {
    platformStatic fun main(args: Array<String>) {
      SpringApplication.run(javaClass<Application>(), *args)
    }
  }
}