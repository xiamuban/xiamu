package com.xiamu.spring.Interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class FullTableOperationInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(FullTableOperationInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor) invocation.getTarget();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        String sql = boundSql.getSql();
        logger.info("拦截SQL:{}",sql);
        boolean containsWhere = !sql.trim().toUpperCase().contains("WHERE");
        // 检查 SQL 是否为全表操作

        if (sql.trim().toUpperCase().startsWith("DELETE") && containsWhere) {
            logger.error(String.format("全表删除拦截：%s", sql));
            throw new RuntimeException("全表删除操作已被拦截!");
        } else if (sql.trim().toUpperCase().startsWith("UPDATE") && containsWhere) {
            logger.error(String.format("全表更新拦截：%s", sql));
            throw new RuntimeException("全表更新操作已被拦截!");
        }

        // 继续执行原方法
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 设置拦截器的一些属性
    }
}
