package documentkeeper;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DbEntity {
    protected boolean isNew;
    public int id;

    public <T> void set(String name, T value){
        try {
            this.getClass().getField(name).set(this, value);
        } catch (Exception ex) {
            System.out.println("DbEntity could not set the variable: " + name + " to: " + value.toString());
            System.out.println("Error: " + ex);
        }
    }

    public DbEntity() {
    }

    public DbEntity(int id) {
        this.id = id;
    }

    protected void ReMap(){

    }

    public void Save(){

    }

//        T instance = null;

//        try {
////            instance = (T)c.getClass().newInstance();
//            ((DbEntity)c).set("id", 123);
//            ((DbEntity)c).set("name", "hello");
//        } catch (Exception ex) {
//            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return instance;

    public static <T> T FindById(int id, Class<T> classType){
        T instance = null;

        try {
            instance = (T)classType.newInstance();
            Db.mapEntity(instance, Db.query("Select * FROM stuff WHERE id="+id));
        } catch (Exception ex) {
            System.out.println("FindById failed: " + ex);
        }

        return instance;
    }

    public static void FindAll(){

    }

    @Override
    public String toString() {
        return "DbEntity " + this.getClass().getName() + " documentkeeper.Stuff[ id=" + id + " ]";
    }
}
