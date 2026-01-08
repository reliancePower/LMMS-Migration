package com.lawsuittracker.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SQLDBConnection {
	public static final Logger logger = Logger.getLogger(SQLDBConnection.class);

	private SQLDBConnection() {
		throw new IllegalStateException("DB Util class");
	}

	public static Connection getDBConnection() {
		Context ctx = null;
		Connection conn = null;
		DataSource ds = null;
		try {
			ctx = new InitialContext();
			Context initCtx = (Context) ctx.lookup("java:/comp/env");
			ds = (DataSource) initCtx.lookup("jdbc/RELCALDB");
//			ds = (DataSource) initCtx.lookup("jdbc/RCBRPL-95002539\\SQLEXPRESS");
			conn = ds.getConnection();

		} catch (NamingException e) {
			logger.error("Not able to connect to DB" + e.getMessage());
		} catch (SQLException e) {

			logger.error("SQL EXCEPTON ==> " + e.getMessage());

		}

		System.out.println();
		return conn;

	}

}