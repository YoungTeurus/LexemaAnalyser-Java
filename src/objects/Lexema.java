package objects;

import java.util.Arrays;
import java.util.List;

public final class Lexema {

    // Указанные ниже лексемы разделяют строку на отдельные лексемы
    public static final List<String> splitters = Arrays.asList(
            "+", "-", "*", "/",
            "=", "(", ")", "^",
            "<", ">", ",", ";",
            "{", "}", ":=", " ",
            "==", "[", "]"
    );

    /**
     * Возвращает True, если переданная строка является началом любого разделяющего оператора.
     */
    public static boolean is_start_of_any_reserved_lexema(String str){
        for (String splitter: splitters) {
            if (splitter.startsWith(str))
                return true;
        }
        return false;
    }

    /**
     * Возвращает True, если переданная строка является любым разделителем.
     */
    public static boolean is_any_reserved_lexema(String str){
        for (String splitter: splitters) {
            if (splitter.equals(str))
                return true;
        }
        return false;
    }

    // Указанные ниже лексемы являются операторами в исходном языке
    public static final List<String> operators = Arrays.asList(
            "not", "xor", "or", "and", "=", ":=",
            "^", "+", "-", "*", "/", "==", "<", ">" 
    );

    // Указанные ниже лексемы являются булевыми константами в исходном языке
    public static final List<String> bool_consts = Arrays.asList(
            "true", "false"
    );

    /**
     * Возможные типы лексем с человеко-понятным описанием.
     * Метод get_description возвращает описание.
     */
    public enum possible_types{
        INT_CONST("Целочисленная константа"),
        FLOAT_CONST("Вещественная константа"),
        VARIABLE("Булева константа"),
        OPERATOR("Переменная"),
        SPLITTER("Оператор"),
        BOOL_CONST("Разделитель");

        private final String _description;

        possible_types(String description){
            _description = description;
        }

        public String get_description(){
            return _description;
        }
    }

    /**
     * Строковое представление лексемы.
     */
    private final String _char;

    public int get_id() {
        return _id;
    }

    /**
     * ID-лексемы - уникальный идентификатор.
     */
    private final int _id;

    public possible_types get_type() {
        return _type;
    }

    /**
     * Тип лексемы.
     * @see possible_types
     */
    private possible_types _type;

    public String get_char() {
        return _char;
    }

    public Lexema(String _char, int _id){
        this._char = _char;
        this._id = _id;

        determine_lexema_type();
    }

    /**
     * Определение типа лексемы из описанных в possible_types.
     * @see possible_types
     */
    private void determine_lexema_type(){
        if (operators.contains(_char)){
            _type = possible_types.OPERATOR;
            return;
        }
        if (splitters.contains(_char)){
            _type = possible_types.SPLITTER;
            return;
        }
        if (bool_consts.contains(_char)){
            _type = possible_types.BOOL_CONST;
            return;
        }
        try{
            Integer.parseInt(_char);
            _type = possible_types.INT_CONST;
            return;
        }
        catch (Exception ignored){}
        try{
            Float.parseFloat(_char);
            _type = possible_types.FLOAT_CONST;
            return;
        }
        catch (Exception ignored){}
        _type = possible_types.VARIABLE;
    }

    @Override
    public String toString() {
        switch (_type){
            case INT_CONST:
            case BOOL_CONST:
            case FLOAT_CONST:
                return "Константа '" + _char +"'";
            case VARIABLE:
                return "Переменная " + _id;
            case OPERATOR:
                return _char;
        }
        return "Нераспознанная лексема " + _id;
    }
}
