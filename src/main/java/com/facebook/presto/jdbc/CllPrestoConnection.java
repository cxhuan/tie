package com.facebook.presto.jdbc;


import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CllPrestoConnection extends PrestoConnection {
	

	CllPrestoConnection(PrestoDriverUri uri, QueryExecutor queryExecutor)
			throws SQLException {
		super(uri, queryExecutor);
	}
	
	
	 	@Override
	    public PreparedStatement prepareStatement(String sql)
	            throws SQLException
	    {
	 		checkOpen();
	 		return new CllPrestoPreparedStatement(this,sql);
	    }
	 	
	 	 private void checkOpen()
	             throws SQLException
	     {
	         if (isClosed()) {
	             throw new SQLException("Connection is closed");
	         }
	     }

	    @Override
	    public CallableStatement prepareCall(String sql)
	            throws SQLException
	    {
	        throw new NotImplementedException("Connection", "prepareCall");
	    }

}
