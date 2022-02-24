/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbHelper;

import componentBean.DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author iDEApAD
 */
public class ConnectionProvider {

    private Connection conn = null;

    public Connection getConnection(DBConnection connection) {
        try {
            Class.forName(connection.getDriverName());
            conn = DriverManager.getConnection(connection.getCustURL(), connection.getUsername(), connection.getPassword());
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            ex.getStackTrace();
        } 
        return null;
    }
    
    public static boolean testConnectionIsOpen(Connection c) throws SQLException{
        return !c.isClosed();
    }

    public Connection getODSConnection(DBConnection connection) throws ClassNotFoundException, SQLException {
        OracleDataSource ods = new OracleDataSource();

        Properties prop = new Properties();
        prop.setProperty("MinLimit", "2");
        prop.setProperty("MaxLimit", "10");

        ods.setURL(connection.getCustURL());
        ods.setUser(connection.getUsername());
        ods.setPassword(connection.getPassword());
        ods.setConnectionCacheProperties(prop);
        ods.setConnectionCachingEnabled(true);
        
        conn = ods.getConnection(connection.getUsername(), connection.getPassword());
        return conn;

    }

}
