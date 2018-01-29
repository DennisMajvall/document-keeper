package documentkeeper.db;

public class Files extends DbEntity{
    public String name;
    public int bytes;

    public Files() {
    }

    public Files(String name) {
        this.name = name;
    }

    public Files(String name, int bytes) {
        this.name = name;
        this.bytes = bytes;
    }

}
