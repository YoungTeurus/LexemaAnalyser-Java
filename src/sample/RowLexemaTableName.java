package sample;

public class RowLexemaTableName {
    public int getId() {
        return id;
    }

    public String getString_reprecentation() {
        return string_reprecentation;
    }

    public String getLexema_type() {
        return lexema_type;
    }

    private final int id;
    private final String string_reprecentation;
    private final String lexema_type;

    public RowLexemaTableName(int id, String string_reprecentation, String lexema_type){
        this.id = id;
        this.string_reprecentation = string_reprecentation;
        this.lexema_type = lexema_type;
    }
}
