package documentkeeper.db;

public class Files extends DbEntity{
    public String name;
    public int bytes;

    public Files() {
    }

    public Files(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Files(Integer id, String name, int bytes) {
        this.id = id;
        this.name = name;
        this.bytes = bytes;
    }

}
