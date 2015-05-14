package com.freetmp.web.login.repository;

import com.freetmp.web.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by LiuPin on 2015/5/14.
 */
public interface UserRepository extends JpaRepository<User,Long> {

  User findByEmail(String email);
}
