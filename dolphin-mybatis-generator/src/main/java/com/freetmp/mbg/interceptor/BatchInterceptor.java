package com.freetmp.mbg.interceptor;

import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.util.Properties;

/**
 * Created by pin on 2015/5/26.
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class BatchInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    CachingExecutor executor = (CachingExecutor) invocation.getTarget();
    return null;
  }

  @Override
  public Object plugin(Object target) {
    if (target instanceof Executor) {
      return Plugin.wrap(target, this);
    } else {
      return target;
    }
  }

  @Override
  public void setProperties(Properties properties) {

  }
}
