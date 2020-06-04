package org.groax.firstApp.persistence;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class PostgresBaseDao {
	protected final Connection getConnection() {
		Connection result = null;
		try {
			InitialContext ic = new InitialContext();
			DataSource datasource = (DataSource) ic.lookup("java:comp/env/jdbc/PostgresDS");
			
			result = datasource.getConnection();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		return result;
	}
}

