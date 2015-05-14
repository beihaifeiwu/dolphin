package com.freetmp.web.login.controller;

import com.freetmp.web.login.entity.User;
import com.freetmp.web.login.model.LoginForm;
import com.freetmp.web.login.repository.UserRepository;
import com.freetmp.web.login.service.CaptchaService;
import com.freetmp.web.login.service.CryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Controller @Slf4j
@SessionAttributes("form")
public class LoginController {

  public static final String CAPTCHA_IMAGE_FORMAT = "jpeg";

  @Autowired UserRepository repository;
  @Autowired CaptchaService captchaService;
  @Autowired CryptService cryptService;

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model) {
    model.addAttribute("form", LoginForm.builder().build());
    return "login";
  }


  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(@Valid @ModelAttribute("form") LoginForm form, BindingResult result,
                      HttpServletRequest request, SessionStatus status) {
    if (result.hasErrors()) {
      form.setCaptcha(null);
      form.setPassword(null);
      return "login";
    }

    String captcha = captchaService.getGeneratedKey(request);
    boolean correct = form.getCaptcha().equalsIgnoreCase(cryptService.md5Hash(captcha));
    if (!correct) {
      result.rejectValue("captcha", "form.captcha", "验证码错误");
      return "login";
    }

    User user = repository.findByEmail(form.getEmail());
    if (user == null) {
      result.rejectValue("email", "form.email", "用户账户不存在");
      return "login";
    }

    correct = cryptService.isPasswordMatch(user, form.getPassword(),captcha);
    if (!correct) {
      result.rejectValue("password", "form.password", "密码不正确");
      return "login";
    }

    status.setComplete();
    return "index";
  }

  @RequestMapping("/captcha")
  public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    captchaService.captcha(request,response);
  }
}
