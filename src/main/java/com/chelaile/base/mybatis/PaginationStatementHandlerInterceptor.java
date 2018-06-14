package com.chelaile.base.mybatis;


import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @author cxhuan
 * 
 **/
@Component
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class ,Integer.class}) })
public class PaginationStatementHandlerInterceptor implements Interceptor {
    private final static Logger logger = LogManager.getLogger(PaginationStatementHandlerInterceptor.class);

    
    public Object intercept(Invocation invocation) throws Throwable {
    	RoutingStatementHandler  statementHandler = (RoutingStatementHandler) invocation.getTarget();
        ParameterHandler parameterHandler = statementHandler.getParameterHandler();
        BoundSql boundSql = statementHandler.getBoundSql();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
        // 没有分页参数
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
        }

        Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
        Dialect dialect = DialectFactory.buildDialect(configuration);
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
       
        // 获取总记录数
        Page<?> page = (Page<?>) rowBounds;
        String countSql = dialect.getCountString(originalSql);
        
        Connection connection = (Connection) invocation.getArgs()[0];
        int total = getTotal(parameterHandler, connection, countSql);
        page.setTotalCount(total);
        int offset = ((page.getPageNo() - 1) * page.getPageSize());
        int limit = page.getPageSize();	
        page.setLimit(limit);
        page.setOffset(offset);
        // 设置物理分页语句
        metaStatementHandler.setValue("delegate.boundSql.sql", dialect.getLimitString(originalSql, offset, limit));
        // 屏蔽mybatis原有分页
        metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        if (logger.isDebugEnabled()) {
            logger.debug("分页SQL : " + boundSql.getSql());
           
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
    	if (target instanceof StatementHandler) {    
            return Plugin.wrap(target, this);    
        } else {    
            return target;    
        }   
    }

    public void setProperties(Properties properties) {
    }

    /**
     * 获取总计录
     * 
     * @param parameterHandler
     * @param connection
     * @param countSql
     * @return
     * @throws Exception
     */
    private int getTotal(ParameterHandler parameterHandler, Connection connection, String countSql) throws Exception {
        // TODO 缓存具有相同SQL语句和参数的总数
        PreparedStatement prepareStatement = connection.prepareStatement(countSql);
        parameterHandler.setParameters(prepareStatement);
        ResultSet rs = prepareStatement.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();
        prepareStatement.close();
        return count;
    }
}