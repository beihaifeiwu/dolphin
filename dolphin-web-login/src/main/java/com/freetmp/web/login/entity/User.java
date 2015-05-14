package com.freetmp.web.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Entity
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {

  @Id @GeneratedValue Long id;

  String password;

  String email;

}
