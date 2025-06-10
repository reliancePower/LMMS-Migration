package com.lawsuittracker.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SecondDBConnection {
    public static Connection getDBConnection() {
        Context ctx = null;
        Connection conn = null;
        DataSource ds = null;
        try {
            ctx = new InitialContext();
            Context initCtx = (Context) ctx.lookup("java:/comp/env");
            ds = (DataSource) initCtx.lookup("jdbc/RCBRPL-95002539\\SQLEXPRESS_SECONDDB");
            conn = ds.getConnection();

        } catch (NamingException e) {
            // Logger can also be new or reused
            System.out.println("Not able to connect to SECOND DB" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL EXCEPTION ==> " + e.getMessage());
        }

        return conn;
    }
}

