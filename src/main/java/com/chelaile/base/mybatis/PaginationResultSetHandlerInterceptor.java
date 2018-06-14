package com.chelaile.base.mybatis;

import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Properties;

/**
 * @author cxhuan
 * 
 **/
@Component
@Intercepts({ @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class PaginationResultSetHandlerInterceptor implements Interceptor {

	/**
	  * @param invocation
	  * @return
	  * @throws Throwable
	  * @author cxhuan
	  */
	public Object intercept(Invocation invocation) throws Throwable {
		DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(resultSetHandler);
        RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("rowBounds");

        Object result = invocation.proceed();

        if (rowBounds instanceof Page) {
            metaStatementHandler.setValue("rowBounds.content", result);
        }
        return result;
	}
	
	/**
	  * @param target
	  * @return
	  * @author cxhuan
	  */
	public Object plugin(Object target) {
		 return Plugin.wrap(target, this);
	}
	
	/**
	  * @param properties
	  * @author cxhuan
	  */
	public void setProperties(Properties properties) {
		
	}

    

}
