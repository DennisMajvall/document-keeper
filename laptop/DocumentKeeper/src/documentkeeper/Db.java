package documentkeeper;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Db {

    private static Connection myConnection = null;
    private static Statement myStatement = null;

    public Db(){
        try {
            String url = "jdbc:derby://localhost:1527/laptop";
            String username = "root";
            String password = "root";

            myConnection = DriverManager.getConnection(url ,username, password);
            myStatement = myConnection.createStatement();
        }
        catch(SQLException ex) {
            System.out.println("Error Db.constructor: " + ex.getMessage());
        }
    }

    public static <T> void mapEntity(T instance, ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int numColumns = meta.getColumnCount();

            while(rs.next()) {
                for (int i = 1; i < numColumns+1; i++) {
                    String colName = rs.getMetaData().getColumnName(i).toLowerCase();
                    ((DbEntity)instance).set(colName, rs.getObject(i));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ResultSet query(String q){
        ResultSet result = null;

        try {
            result = myStatement.executeQuery(q);
        } catch (SQLException ex) {
            System.out.println("Error Db.query: " + q);
        }

        return result;
    }
}
