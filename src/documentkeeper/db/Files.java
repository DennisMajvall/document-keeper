package documentkeeper.db;

public class Files extends DbEntity{
    public String name;
    public long checksum;

    public Files() {
    }

    public Files(String name) {
        this.name = name;
    }

    public Files(String name, long checksum) {
        this.name = name;
        this.checksum = checksum;
    }

}
