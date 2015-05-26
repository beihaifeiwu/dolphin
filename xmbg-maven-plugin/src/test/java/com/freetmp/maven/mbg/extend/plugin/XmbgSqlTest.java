package com.freetmp.maven.mbg.extend.plugin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by LiuPin on 2015/5/26.
 */
public class XmbgSqlTest extends XmbgBaseTest {

  @Autowired JdbcTemplate template;

  @Test
  public void testBatchUpsertSql() throws SQLException {
    String sql = "insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        ";\ninsert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?";

    Object[] args = new Object[]{
        3, "tester_3", "Tester_3", "691b14d79bf0fa2215f155235df5e670b64394cc", "7efbd59d9741d34f", "user", "2015-05-26 17:25:49.009",
        3, "tester_3", "Tester_3", "691b14d79bf0fa2215f155235df5e670b64394cc", "7efbd59d9741d34f", "user", "2015-05-26 17:25:49.009",
        4, "tester_4", "Tester_4", "691b14d79bf0fa2215f155235df5e670b64394cc", "7efbd59d9741d34f", "user", "2015-05-26 17:25:49.009",
        4, "tester_4", "Tester_4", "691b14d79bf0fa2215f155235df5e670b64394cc", "7efbd59d9741d34f", "user", "2015-05-26 17:25:49.009"
    };
    Connection connection = template.getDataSource().getConnection();
    PreparedStatement ps = connection.prepareStatement(sql);
    for (int i = 1; i <= args.length; i++) {
      ps.setObject(i, args[i - 1]);
    }

    ps.execute();
  }

}
