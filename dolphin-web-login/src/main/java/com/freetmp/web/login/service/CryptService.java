package com.freetmp.web.login.service;

import com.freetmp.web.login.entity.User;
import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * Created by LiuPin on 2015/5/14.
 */
@Service
public class CryptService {

  Des des = new Des();

  public String md5Encrypt(@NonNull String password, @NonNull Long id){
    return DigestUtils.md5Hex(password + id);
  }

  public String md5Hash(@NonNull String captcha){
    return DigestUtils.md5Hex(captcha);
  }

  public boolean isPasswordMatch(@NonNull User user, @NonNull String password, String captcha){
    String dec = desDecrypt(password, captcha);
    String encrypted = md5Encrypt(dec, user.getId());
    return encrypted.equals(user.getPassword());
  }

  public String desDecrypt(@NonNull String password, @NonNull String key){
    return des.strDec(password,key,null,null);
  }
}
