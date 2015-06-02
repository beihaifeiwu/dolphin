package com.freetmp.mbg.merge

import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/15.
 */
class MethodMergeSpec extends Specification {

  def "Add new Method for interface"() {
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

  def "Add new Method for Class"() {
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

  def "a lot of methods merge"(){
    expect:
    CompilationUnitMerger.merge(first, second).trim() == result.trim()
    where:
    first =
"""
package com.freetmp.xmbg.test.mapper;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int batchInsert(List<User> list);

    int batchUpdate(List<User> list);

    int upsert(@Param("record") User record, @Param("array") String[] array);

    int upsertSelective(@Param("record") User record, @Param("array") String[] array);
}
"""
    second = """
package com.freetmp.xmbg.test.mapper;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int batchInsert(List<User> list);

    int batchUpdate(List<User> list);
}
"""
    result =
"""
package com.freetmp.xmbg.test.mapper;

import com.freetmp.xmbg.test.entity.User;
import com.freetmp.xmbg.test.entity.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int batchInsert(List<User> list);

    int batchUpdate(List<User> list);

    int upsert(@Param("record") User record, @Param("array") String[] array);

    int upsertSelective(@Param("record") User record, @Param("array") String[] array);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Long id);
}
"""
  }

  def "test on complex method"() {
    expect:
    CompilationUnitMerger.merge(first, second).trim() == result.trim()
    where:
    first =
        """
package com.freetmp.xmbg.test.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append(", salt=").append(salt);
        sb.append(", roles=").append(roles);
        sb.append(", registerDate=").append(registerDate);
        sb.append("]");
        return sb.toString();
    }
}
"""
    second =
        """
package com.freetmp.xmbg.test.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append(", salt=").append(salt);
        sb.append(", roles=").append(roles);
        sb.append(", registerDate=").append(registerDate);
        sb.append("]");
        return sb.toString();
    }
}
"""
    result =
        """
package com.freetmp.xmbg.test.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new  StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append(", salt=").append(salt);
        sb.append(", roles=").append(roles);
        sb.append(", registerDate=").append(registerDate);
        sb.append("]");
        return sb.toString();
    }
}
"""
  }
}
