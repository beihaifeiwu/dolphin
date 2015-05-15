package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/15.
 */
class MethodMergeSpec extends Specification{

    def "Add new Method for interface"(){
        expect:
            CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first =
"""
package com.freetmp.web.login.repository;

import com.freetmp.web.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by LiuPin on 2015/5/14.
 */
public interface UserRepository extends JpaRepository<User,Long> {

  User findByEmail(String email);

  void delete(User user);
}
"""
        second =
"""
package com.freetmp.web.login.repository;

import com.freetmp.web.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by LiuPin on 2015/5/14.
 */
public interface UserRepository extends JpaRepository<User,Long> {

  User findByEmail(String email);

  void save(User user);
}
"""
        result =
"""
package com.freetmp.web.login.repository;

import com.freetmp.web.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by LiuPin on 2015/5/14.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    void delete(User user);

    void save(User user);
}
"""
    }

    def "Add new Method for Class"(){
        expect:
            CompilationUnitMerger.merge(first, second).trim() == result.trim()
        where:
        first =
"""
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

  public String desDecrypt(@NonNull String password, @NonNull String key){
    return des.strDec(password,key,null,null);
  }
}
"""
        second =
"""
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
"""
        result =
"""
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

    Des des = new  Des();

    public String md5Encrypt(@NonNull String password, @NonNull Long id) {
        return DigestUtils.md5Hex(password + id);
    }

    public String md5Hash(@NonNull String captcha) {
        return DigestUtils.md5Hex(captcha);
    }

    public String desDecrypt(@NonNull String password, @NonNull String key) {
        return des.strDec(password, key, null, null);
    }

    public boolean isPasswordMatch(@NonNull User user, @NonNull String password, String captcha) {
        String dec = desDecrypt(password, captcha);
        String encrypted = md5Encrypt(dec, user.getId());
        return encrypted.equals(user.getPassword());
    }
}
"""
    }
}
