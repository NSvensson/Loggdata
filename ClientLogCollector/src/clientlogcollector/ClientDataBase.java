package clientlogcollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class ClientDataBase {
    /*
    This class will focus on all interactions with the database server, 
    this is to prevent having to overuse try clusters and SQL syntax in the
    other parts of this application.
    */
    
    private String database_URI;
    private String database_username;
    private String database_password;
    private final String database_URI_driver = "jdbc:mysql://";
    private final String database_driver = "com.mysql.jdbc.Driver";
    private Connection database_connection = null;
    public String error_message = null;

    /*
    When connecting to a database server the following 4 requrements
    needs to be met:
    database_URI, the URI pointing towards which database to connect to.
    database_username, the username for the account you want to connect with.
    database_password, the password for the acocunt you want to connect with.
    database_driver, the database_driver is a string hinting towards which
    class the JDBC connector needs to search for to find the proper driver for
    the database you're connecting to.
    
    Since this application is focusing on working
    towards a MySQL server, the driver can be hardcoded.
    The default driver for a MySQL server is written as following:
    "com.mysql.jdbc.Driver"
    */

    public ClientDataBase(String database_URI, String database_username, String database_password) {
        this.database_URI = this.database_URI_driver + database_URI;
        this.database_username = database_username;
        this.database_password = database_password;

        /*
        When initiating a new ClientDataBase object, it should be written in
        the following manner.
        
        ClientDataBase cdb = new ClientDataBase(
                "localhost:3306/Database_name", 
                "username", 
                "password");
        */
    }

    public void password(String database_password) { this.database_password = database_password; }
    public void username(String database_username) { this.database_username = database_username; }
    public void databaseURI(String database_URI) { this.database_URI = this.database_URI_driver + database_URI; }
    
    public void connect() {
        /*
        This method uses the connection information that has been provided to
        the class variables to connect to the database server.
        
        If there already is an active connection, this method can also be used
        to reconnect to the database.
        */
        
        if (database_connection != null) close();
        
        try {
            if (database_driver != null) Class.forName(database_driver);
            
            database_connection = DriverManager.getConnection(
                    database_URI, 
                    database_username,
                    database_password);
            
        } catch (SQLException | ClassNotFoundException se) {
            error_message = se.getMessage();
            
            try { if (database_connection != null) database_connection.close(); }
            catch (SQLException e) {}
        }
    }
    
    public void close() {
        /*
        This method closes the current connection towards the database server.
        */
        try { if (database_connection != null) database_connection.close(); }
        catch (SQLException e) {}
    }
    
    public List<List<String>> select(String[] columns, String table, String designated_column, Object designated_value){
        /*
        This method will be used to select and return the values from the
        columns provided by the columns array from the provided table.
        
        The arguments designated_column and designated_value is not required for
        this method, however it is useful in case the need for a WHERE clause
        appears.
        
        The reason behind using the List library to return the values found
        by the provided query is simply because it is easier to work with when
        obtaining the values from the resultset, this however is free and might
        change during the development of this method.
        */

        List<List<String>> results = new ArrayList<>();
        String query = "SELECT ";
        
        if (columns.length >= 2) {
            for (String column: columns) {
                query += column + ", ";
            }

            if (query.endsWith(", ")){
                query = query.substring(0, query.length() - 2) + " FROM " + table;
            }
        } else {
            query += columns[0] + " FROM " + table;
        }
        
        if (designated_column != null && designated_value != null) {
            query += " WHERE " + designated_column + "=?";
        }
        
        query += ";";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = database_connection.prepareStatement(query);
            if (designated_column != null && designated_value != null) {
                stmt.setObject(1, designated_value);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                List<String> row = new ArrayList<>(columns.length);
                
                for (String column : columns) {
                    row.add(rs.getString(column));
                }
                results.add(row);
            }
        } catch (SQLException se) {
            error_message = se.getMessage();
        } finally {
            try { if (rs != null) rs.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
            
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
        return results;
    }

    public List<List<String>> select(String[] columns, String table){
        return select(columns, table, null, null);
    }
    
    public void create_table(String service) {
        /*
        If a new service is being added, this method will be called upon to
        create a table to store the logs from the new service.

        This method is currently only useful for the creation of new tables
        to store the logs in.
        */
        String query = "CREATE TABLE " + 
                service + " (" +
                "id INTEGER NOT NULL AUTO_INCREMENT, " +
                "date DATETIME NOT NULL, " +
                "action VARCHAR(255) NOT NULL, " +
                "PRIMARY KEY (id));";
        
        PreparedStatement stmt = null;
        try {
            stmt = database_connection.prepareStatement(query);
            stmt.executeUpdate();

        } catch (SQLException se) {
            error_message = se.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
    }

    public void insert(String[] columns, Object[][] values, String table) {
        /*
        This method will be used to insert the given values into the columns of
        the desired table.
        
        The arguments provided will work in the following manner:
        +--------------------------------------+
        |                table                 |
        +------------+------------+------------+
        | columns[0] | columns[1] | columns[2] |
        +------------+------------+------------+
        |values[0][0]|values[0][1]|values[0][2]|
        +------------+------------+------------+
        |values[1][0]|values[1][1]|values[1][2]|
        +------------+------------+------------+
        |values[2][0]|values[2][1]|values[2][2]|
        +------------+------------+------------+
        |values[3][0]|values[3][1]|values[3][2]|
        +------------+------------+------------+
        
        The table string will contain the name of the table you're inserting into.
        The columns string array will contain the names of the columns you're insering into.
        The values object array will contain the values of the columns you're inserting.
        
        The inner arrays of the values array should always contain the same amount
        of values as the columns you're inserting into, aswell as ordered in the
        same order as the columns you're inserting into.
        */

        String query = "INSERT INTO " + table + "(";

        if (columns.length == 1) {
            query += columns[0] + ") VALUES ( ? );";
        } else if (columns.length > 1) {
            for (String column : columns) {
                query += column + ", ";
            }

            if (query.endsWith(", ")){
                query = query.substring(0, query.length() - 2) + ") VALUES (";
            }

            for (String column : columns) {
                query += "?, ";
            }

            if (query.endsWith(", ")){
                query = query.substring(0, query.length() - 2) + ");";
            }
        }
        
        PreparedStatement stmt = null;
        try {
            stmt = database_connection.prepareStatement(query);
            for (Object[] row : values) {
                for (int i = 0; i < row.length; i++) {
                    stmt.setObject(i + 1, row[i]);
                }
                stmt.executeUpdate();
            }

        } catch (SQLException  se) {
            error_message = se.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
    }
    
    public void update(String[] columns, Object[] values, String table, String designated_column, Object designated_value) {
        /*
        This method will be used to change values on the designated row in the
        provided table.
        
        The string table will contain the name of the table to update.
        Column names and values need to be provided in way similar to how 
        they're provided in the insert method.
        The only difference is that the values string array provided in
        this method will only contain values for one row.
        
        The arguments designated_column and designated_value is not required for
        this method, however it is useful in case the need for a WHERE clause
        appears.
        */
        String query = "UPDATE " + table + " SET ";

        for (String column : columns) {
            query += column + "=?, ";
        }
        
        if (query.endsWith(", ")){
            query = query.substring(0, query.length() - 2);
        }
        
        if (designated_column != null && designated_value != null) {
            query += " WHERE " + designated_column + "=?";
        }
        
        query += ";";
        
        PreparedStatement stmt = null;
        try {
            stmt = database_connection.prepareStatement(query);
            
            int i;
            for (i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }
            if (designated_column != null && designated_value != null) {
                stmt.setObject(i + 1, designated_value);
            }
            stmt.executeUpdate();

        } catch (SQLException  se) {
            error_message = se.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
    }
    
    public void update(String[] columns, Object[] values, String table) {
        update(columns, values, table, null, null);
    }

    public void drop_table(String table) {
        /*
        This method will be used when removing a table from the database
        is desired. Removing a table is irreversible, use with caution.
        
        The string table will contain the name of the desired table to drop.
        */
        String query = "DROP TABLE " + table + ";";
        
        PreparedStatement stmt = null;
        try {
            stmt = database_connection.prepareStatement(query);
            stmt.executeUpdate();

        } catch (SQLException se) {
            error_message = se.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
    }
    
    public void delete_row(String table, String designated_column, Object designated_value) {
        /*
        This method will be called upon when it is desired to delete
        the designated row from the chosen table.
        
        The arguments designated_column and designated_value is required for
        this method, if a WHERE clause isn't provided when using a DELETE FROM 
        query, it will cause everything in the chosen table to be deleted.
        Such a removal is not required in this application, thus it will only be
        implemented if the need for it arises.
        */
        String query = "DELETE FROM " + table + 
                " WHERE " + designated_column + "=?;";
        
        PreparedStatement stmt = null;
        try {
            stmt = database_connection.prepareStatement(query);
            stmt.setObject(1, designated_value);
            stmt.executeUpdate();

        } catch (SQLException se) {
            error_message = se.getMessage();
        } finally {
            try { if (stmt != null) stmt.close(); }
            catch (SQLException se) { error_message = se.getMessage(); }
        }
    }
}