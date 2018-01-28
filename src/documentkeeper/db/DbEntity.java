package documentkeeper.db;

import java.util.ArrayList;

public abstract class DbEntity {
    protected boolean isNew;
    public int id;

    protected DbEntity() {
        isNew = true;
    }

    public void Save(){

    }

    /*

        Find - singular

    */

    public static <T> T FindById(int id, Class<T> classType){
        try {
            String q = "Select * FROM " + getClassName(classType) + " WHERE id="+id;
            return Db.get().getEntity(classType, Db.get().query(q));
        } catch (Exception ex) {
            System.out.println("FindById failed: " + ex);
        }

        return null;
    }

    public static <T> T FindOne(String query, Class<T> classType){
        if (query.trim().toUpperCase().indexOf("SELECT") > 0) {
            query = "SELECT " + query.trim();
        }
        try {
            return Db.get().getEntity(classType, Db.get().query(query));
        } catch (Exception ex) {
            System.out.println("FindOne failed: " + ex);
        }

        return null;
    }

    /*

        Find - plural

    */

    public static <T> ArrayList<T> FindAll(Class<T> classType){
        String q = "Select * FROM " + getClassName(classType);

        try {
            return Db.get().getEntities(classType, Db.get().query(q));
        } catch (Exception ex) {
            System.out.println("FindAll failed: " + q + " " + ex);
        }

        return new ArrayList<>();
    }

    public static <T> ArrayList<T> Find(String query, Class<T> classType){
        if (query.trim().toUpperCase().indexOf("SELECT") > 0) {
            query = "SELECT " + query.trim();
        }
        try {
            return Db.get().getEntities(classType, Db.get().query(query));
        } catch (Exception ex) {
            System.out.println("Find failed: " + query + " " + ex);
        }

        return new ArrayList<>();
    }

    public static <T> ArrayList<T> FindWhere(String columns, String tables, String condition, Class<T> classType){
        String q = "SELECT " + columns + " FROM " + tables + " WHERE " + condition;
        return Find(q, classType);
    }

    public static <T> ArrayList<T> FindWhere(String tables, String condition, Class<T> classType){
        String q = "SELECT * FROM " + tables + " WHERE " + condition;
        return Find(q, classType);
    }

    public static <T> ArrayList<T> FindWhere(String condition, Class<T> classType){
        String q = "SELECT * FROM " + getClassName(classType) + " WHERE " + condition;
        return Find(q, classType);
    }

    /*

        Misc

    */

    public <T> void set(String name, T value){
        try {
            this.getClass().getField(name).set(this, value);
        } catch (Exception ex) {
            System.out.println("DbEntity could not set the variable: " + name + " to: " + value.toString());
            System.out.println("Error: " + ex);
        }
    }

    private static <T> String getClassName(Class<T> classType){
        return classType.getName().substring(classType.getName().lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public String toString() {
        return "DbEntity " + this.getClass().getName() + " documentkeeper.Stuff[ id=" + id + " ]";
    }
}
