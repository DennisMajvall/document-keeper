package documentkeeper.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** SINGLETON, syntax: Db.get().method(...); */
public class Db {

    private static Db myInstance = null;

    private Statement myStatement = null;
    private Statement myUpdatableStatement = null;
    private Connection myConnection = null;

    private HashMap <String, String[]> tableColumns = new HashMap <>();
    private HashMap <String, int[]> tableColumnTypes = new HashMap <>();

    public static Db get() {
      if(myInstance == null) { myInstance = new Db(); }
      return myInstance;
    }

    private Db(){
        try {
            String url = "jdbc:derby://localhost:1527/doc";
            String username = "root";
            String password = "root";

            myConnection = DriverManager.getConnection(url ,username, password);
            myStatement = myConnection.createStatement();
            myUpdatableStatement = myConnection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
    }

    public ResultSet query(String q){ return query(q, myStatement); }
    public ResultSet query(String q, Statement statement){
        ResultSet result = null;

        try {
            result = statement.executeQuery(q);
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }

        return result;
    }

    public boolean update(DbEntity entity, String tableName) {
        if (!tableColumns.containsKey(tableName)) {
            fetchColumnNames(tableName);
        }

        String query = "UPDATE " + tableName + " SET ";
        String[] colNames = tableColumns.get(tableName);
        for (String colName : colNames) {
            query += colName + "=?, ";
        }
        query = query.substring(0, query.length()-2);
        query += " WHERE id =" + entity.get("id");
        return executeUpdateInsert(query, entity, tableName);
    }

    public boolean insert(DbEntity entity, String tableName) {
        if (!tableColumns.containsKey(tableName)) {
            fetchColumnNames(tableName);
        }

        String query = "INSERT INTO " + tableName + " (";
        String[] colNames = tableColumns.get(tableName);
        for (String colName : colNames) {
            query += colName + ", ";
        }
        query = query.substring(0, query.length()-2);
        query += ") VALUES (" ;
        for (String colName : colNames) {
            query += "?, ";
        }
        query = query.substring(0, query.length()-2);
        query += ")";

        boolean result = executeUpdateInsert(query, entity, tableName);
        if (result) { entity.isNew = false; }
        return result;
    }

    public boolean delete(DbEntity entity, String tableName){
        ResultSet rs = query("SELECT id FROM " + tableName + " WHERE id=" + entity.get("id"), myUpdatableStatement);
        try {
            rs.first();
            rs.deleteRow();
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
        return true;
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
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }

        return null;
    }

    public <T> ArrayList<T> getEntities(Class<T> classType, ResultSet rs) {
        ArrayList<T> instances = new ArrayList<>();

        try {
            boolean hasResults = rs.next();
            if (!hasResults) {
                return instances;
            }
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

    private void setStatementVariable(PreparedStatement statement, int index, Object val, int type){
        try {
            switch (type) {
                case java.sql.Types.VARCHAR:
                case java.sql.Types.CHAR:
                    statement.setString(index, (String)val);
                    break;
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                    statement.setInt(index, (int)val);
                    break;
                case java.sql.Types.BIGINT:
                    statement.setLong(index, (long)val);
                    break;
                case java.sql.Types.FLOAT:
                case java.sql.Types.NUMERIC:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.DECIMAL:
                    statement.setDouble(index, (double)val);
                    break;
                case java.sql.Types.BOOLEAN:
                case java.sql.Types.BIT:
                    statement.setBoolean(index, (boolean)val);
                    break;
                default:
                    System.out.println("could not set variable of type: " + type);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
//            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
    }

    private boolean executeUpdateInsert(String q, DbEntity entity, String tableName){

        try {
            PreparedStatement statement = myConnection.prepareStatement(q, new String[] { "id"} );
            String[] colNames = tableColumns.get(tableName);
            int[] colTypes = tableColumnTypes.get(tableName);

            for (int i = 0; i < colNames.length; i++) {
                setStatementVariable(statement, i+1, entity.get(colNames[i]), colTypes[i]);
            }

            int rowsUpdated = 0;
            try {
                rowsUpdated = statement.executeUpdate();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
    //            Logger.getGlobal().log(Level.SEVERE, null, ex);
            }

            ResultSet rs = statement.getGeneratedKeys();
            if(rs != null && rs.next()) {
                ((DbEntity)entity).set("id", rs.getInt(1));
            }

            return rowsUpdated > 0;
        } catch (Exception ex) { Logger.getGlobal().log(Level.WARNING, null, ex); }

        return false;
    }

    private String getFirstRowQuery(String tableName){
        // Java derby does not support LIMIT
        return "SELECT * FROM (" +
            "SELECT " + tableName + ".*, " +
            "ROW_NUMBER() OVER() AS rownumfakelimit " +
            "FROM " + tableName +
        ") AS tmp WHERE rownumfakelimit <= 1";
    }

    private void fetchColumnNames(String tableName){
        // Java derby does not support INFORMATION_SCHEMA
        try {
            ResultSet rs = query(getFirstRowQuery(tableName));
            ResultSetMetaData meta = rs.getMetaData();

            int numColumns = meta.getColumnCount();

            // Don't save id and rownumfakelimit as column names
            String[] colNames = new String[numColumns - 2];
            int[] colTypes = new int[numColumns - 2];

            for (int i = 1, lastInsertIndex = 0; i < numColumns+1; i++) {
                String colName = meta.getColumnName(i).toLowerCase();
                if (!colName.equals("id") && !colName.equals("rownumfakelimit")){
                    colTypes[lastInsertIndex] = meta.getColumnType(i);
                    colNames[lastInsertIndex++] = colName;
//                    System.out.println("COL ("+lastInsertIndex+"): " + colName);
                }
            }
            tableColumns.put(tableName, colNames);
            tableColumnTypes.put(tableName, colTypes);
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
    }

    private <T> boolean setColumns(T instance, ResultSet rs, ArrayList<String> colNames) {
        try {
            for (String colName : colNames) {
                ((DbEntity)instance).set(colName, rs.getObject(colName));
            }
            return true;
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
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
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
        return result;
    }

}
