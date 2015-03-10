package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.mbg.plugin.page.MySqlPaginationPlugin;
import com.freetmp.mbg.plugin.page.PostgreSQLPaginationPlugin;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.PluginConfiguration;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by LiuPin on 2015/2/3.
 */
public class XmbgMojoUnitTest {

    @SuppressWarnings("unchecked")
    protected boolean checkPlugin(Class<?> clazz, Context context){
        Field field = FieldUtils.getDeclaredField(Context.class,"pluginConfigurations",true);
        try {
            List<PluginConfiguration> pluginConfigurations = (List<PluginConfiguration>) field.get(context);
            for(PluginConfiguration pluginConfiguration : pluginConfigurations){
                if(pluginConfiguration.getConfigurationType().equals(clazz.getTypeName())){
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Test
    public void testChoosePagination(){
        Context context = new Context(null);
        //测试PostgreSQL
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL("jdbc:postgresql://10.1.8.61:5432/DataManageSystem");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        MyBatisGeneratorMojo mbgm = new MyBatisGeneratorMojo();
        mbgm.choosePaginationPlugin(context);
        assertTrue(checkPlugin(PostgreSQLPaginationPlugin.class,context));
        
        //测试MySql
        jdbcConnectionConfiguration.setConnectionURL("jdbc:mysql://localhost/web-monitor?useUnicode=true&characterEncoding=utf-8");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        mbgm.choosePaginationPlugin(context);
        assertTrue(checkPlugin(MySqlPaginationPlugin.class,context));
    }
}
