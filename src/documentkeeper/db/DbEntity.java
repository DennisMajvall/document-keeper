package documentkeeper.db;

import java.util.ArrayList;

public abstract class DbEntity {
    protected boolean isNew = true;
    public int id = 0;

    protected DbEntity() {}

// <editor-fold defaultstate="collapsed" desc="Find - singular">

    public static <T> T findById(int id, Class<T> classType){
        String q = "Select * FROM " + getClassName(classType) + " WHERE id="+id;
        return Db.get().getEntity(classType, Db.get().query(q));
    }

    public static <T> T findOne(String query, Class<T> classType){
        if (query.trim().toUpperCase().indexOf("SELECT") > 0) {
            query = "SELECT " + query.trim();
        }
        
        return Db.get().getEntity(classType, Db.get().query(query));
    }

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Find - plural">

    public static <T> ArrayList<T> findAll(Class<T> classType){
        String q = "Select * FROM " + getClassName(classType);
        return Db.get().getEntities(classType, Db.get().query(q));
    }

    public static <T> ArrayList<T> find(String query, Class<T> classType){
        if (query.trim().toUpperCase().indexOf("SELECT") > 0) {
            query = "SELECT " + query.trim();
        }
        return Db.get().getEntities(classType, Db.get().query(query));
    }

    public static <T> ArrayList<T> findWhere(String columns, String tables, String condition, Class<T> classType){
        String q = "SELECT " + columns + " FROM " + tables + " WHERE " + condition;
        return find(q, classType);
    }

    public static <T> ArrayList<T> findWhere(String tables, String condition, Class<T> classType){
        String q = "SELECT * FROM " + tables + " WHERE " + condition;
        return find(q, classType);
    }

    public static <T> ArrayList<T> findWhere(String condition, Class<T> classType){
        String q = "SELECT * FROM " + getClassName(classType) + " WHERE " + condition;
        return find(q, classType);
    }

// </editor-fold>

    public boolean save(){
        if (isNew) {
            return Db.get().insert(this, getClassName(this.getClass()));
        } else {
            return Db.get().update(this, getClassName(this.getClass()));
        }
    }

    public boolean delete(){
        if (!isNew && id != 0) {
            boolean result = Db.get().delete(this, getClassName(this.getClass()));
            isNew = true;
            id = 0;
            return result;
        }
        return false;
    }

    public <T> void set(String name, T value){
        try {
            this.getClass().getField(name).set(this, value);
        } catch (Exception ex) {
            System.out.println("DbEntity could not set the variable: " + name + " to: " + value.toString());
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public <T> T get(String name){

        try {
            return (T)this.getClass().getField(name).get(this);
        } catch (Exception ex) {
            System.out.println("DbEntity could not get the variable: " + name);
            java.util.logging.Logger.getGlobal().log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    private static <T> String getClassName(Class<T> classType){
        return classType.getName().substring(classType.getName().lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public String toString() {
        return "DbEntity " + this.getClass().getName() + " documentkeeper.Stuff[ id=" + id + " ]";
    }
}
