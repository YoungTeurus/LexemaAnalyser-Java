package sample;

public class RowHashTableName {
    public int getId() {
        return id;
    }

    public String getString_reprecentation() {
        return string_reprecentation;
    }

    private final int id;
    private final String string_reprecentation;

    public RowHashTableName(int id, String string_reprecentation){
        this.id = id;
        this.string_reprecentation = string_reprecentation;
    }
}
