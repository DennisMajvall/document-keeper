package documentkeeper;


public class Stuff extends DbEntity{
    public String name;

    public Stuff() {
    }

    public Stuff(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

}
