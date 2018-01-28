package documentkeeper.db;

import java.sql.*;
import java.util.ArrayList;

/**
 * SINGLETON, syntax: Db.get().method(...);
 */
public class Db {

    private Statement myStatement = null;

    private static Db myInstance = null;

    public static Db get() {
      if(myInstance == null) { myInstance = new Db(); }
      return myInstance;
    }
    
    private Db(){
        try {
            String url = "jdbc:derby://localhost:1527/doc";
            String username = "root";
            String password = "root";
            
            Connection connection = DriverManager.getConnection(url ,username, password);
            myStatement = connection.createStatement();
        }
        catch(Exception ex) {
            System.out.println("Error Db.constructor: " + ex);
        }
    }
    
    private <T> boolean setColumns(T instance, ResultSet rs, ArrayList<String> colNames) {
        try {
            for (String colName : colNames) {
                ((DbEntity)instance).set(colName, rs.getObject(colName));
            }
            return true;
        } catch (SQLException ex) { System.out.println("Error Db.setColumns: " + ex); }
        return false;
    }
    
    private ArrayList<String> getColNames(ResultSet rs) {
        ArrayList<String> result = new ArrayList();

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int numColumns = meta.getColumnCount();
            
            for (int i = 1; i < numColumns+1; i++) {
                result.add(meta.getColumnName(i).toLowerCase());
            }
        } catch (SQLException ex) { System.out.println("Error Db.getColNames: " + ex); }
        return result;
    }

    public <T> T getEntity(Class<T> classType, ResultSet rs) {
        try {
            if (!rs.next()) { return null; }

            T instance = (T)classType.newInstance();
            ((DbEntity)instance).isNew = false;
            ArrayList<String> colNames = getColNames(rs);
            if (setColumns(instance, rs, colNames)) {
                return instance;
            }
        } catch (Exception ex) { System.out.println("Error Db.getEntity: " + ex); }

        return null;
    }

    public <T> ArrayList<T> getEntities(Class<T> classType, ResultSet rs) {
        ArrayList<T> instances = new ArrayList<>();

        try {
            rs.next();
            ArrayList<String> colNames = getColNames(rs);
            
            do {
                T instance = (T)classType.newInstance();
                ((DbEntity)instance).isNew = false;
                if (setColumns(instance, rs, colNames)) {
                    instances.add(instance);
                }
            } while(rs.next());

        } catch (Exception ex) { System.out.println("Error Db.getEntities: " + ex); }

        return instances;
    }

    public <T> void mapEntities(ArrayList<T> instances, ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int numColumns = meta.getColumnCount();

            ArrayList<String> colNames = getColNames(rs);
//            setColumns(instance, rs, colNames);


            while(rs.next()) {
                for (int i = 1; i < numColumns+1; i++) {
                    String colName = rs.getMetaData().getColumnName(i).toLowerCase();
                    ((DbEntity)instances.get(0)).set(colName, rs.getObject(i));
                }
            }
        } catch (SQLException ex) { System.out.println("Error Db.mapEntities: " + ex); }
    }

    public ResultSet query(String q){
        ResultSet result = null;

        try {
            result = myStatement.executeQuery(q);
        } catch (Exception ex) { System.out.println("Error Db.query: " + q + ". Msg: " + ex); }

        return result;
    }
}
