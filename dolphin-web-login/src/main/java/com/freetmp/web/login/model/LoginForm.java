package com.freetmp.web.login.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Data @Builder
public class LoginForm {

  @NotNull @Email String email;

  @NotNull String password;

  @NotNull String captcha;
}
