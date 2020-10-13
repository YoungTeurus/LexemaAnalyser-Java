package objects.LexemaParcer;

import java.util.Arrays;
import java.util.List;

public final class Lexema {

    // Указанные ниже лексемы разделяют строку на отдельные лексемы
    public static final List<String> splitters = Arrays.asList(
            "+", "-", "*", "/",
            "=", "(", ")",
            "<", ">", ",", ";",
            "{", "}", ":=", " ", "!=",
            "==", "[", "]"
    );

    /**
     * Возвращает True, если переданная строка является началом любого разделяющего оператора.
     */
    public static boolean is_start_of_splitter(String str){
        for (String splitter: splitters) {
            if (splitter.startsWith(str))
                return true;
        }
        return false;
    }

    /**
     * Возвращает True, если переданная строка является любым разделителем.
     */
    public static boolean is_splitter(String str){
        for (String splitter: splitters) {
            if (splitter.equals(str))
                return true;
        }
        return false;
    }

    // Указанные ниже лексемы являются операторами в исходном языке
    public static final List<String> operators = Arrays.asList(
            "if", "else",
            "NOT", "XOR", "OR", "AND", "=", ":=",
            "+", "-", "*", "/", "==", "<", ">", "!="
    );

    // Указанные ниже лексемы являются булевыми константами в исходном языке
    public static final List<String> bool_consts = Arrays.asList(
            "true", "false"
    );

    /**
     * Возможные типы лексем с человеко-понятным описанием.
     * Метод get_description возвращает описание.
     */
    public enum lexema_types {
        INT_CONST("Целочисленная константа"),
        FLOAT_CONST("Вещественная константа"),
        VARIABLE("Переменная"),
        OPERATOR("Оператор"),
        SPLITTER("Разделитель"),
        BOOL_CONST("Булева константа"),
        BLOCK_POINTER("Указатель на блок кода");

        private final String _description;

        lexema_types(String description){
            _description = description;
        }

        public String get_description(){
            return _description;
        }
    }



    // Собственно описание класса:


    /**
     * Строковое представление лексемы.
     */
    private final       String          _char;

    /**
     * ID-лексемы - уникальный идентификатор.
     */
    private final       int             _id;

    /**
     * Тип лексемы.
     * @see lexema_types
     */
    private             lexema_types    _type;

    /**
     * Значение лексемы
     */
    public              Object          _value;

    // Конструкторы:
    public              Lexema(String _char, int _id){
        this._char = _char;
        this._id = _id;

        determine_lexema_type_and_value();
    }

    public              Lexema(String _char, int _id, lexema_types _type){
        this._char = _char;
        this._id = _id;
        this._type = _type;
    }

    public              Lexema(Lexema other){
        _id = other._id;
        _char = other._char;
        _type = other._type;
        _value = other._value;
    }

    public              Object          get_value(){
        return _value;
    }

    // Get-теры.
    public              int             get_id() {
        return _id;
    }
    public              lexema_types    get_type() {
        return _type;
    }
    public              String          get_char() {
        return _char;
    }

    /**
     * Определение типа лексемы из описанных в possible_types.
     * @see lexema_types
     */
    private             void determine_lexema_type_and_value(){
        if (operators.contains(_char)){
            _type = lexema_types.OPERATOR;
            _value = _char;
            return;
        }
        if (splitters.contains(_char)){
            _type = lexema_types.SPLITTER;
            _value = _char;
            return;
        }
        if (bool_consts.contains(_char)){
            _type = lexema_types.BOOL_CONST;
            _value = _char.equals("true");
            return;
        }
        try{
            _value = Integer.parseInt(_char);
            _type = lexema_types.INT_CONST;
            return;
        }
        catch (Exception ignored){}
        try{
            _value = Float.parseFloat(_char);
            _type = lexema_types.FLOAT_CONST;
            return;
        }
        catch (Exception ignored){}
        _type = lexema_types.VARIABLE;
        _value = "$VAR_" + _char;
    }

    @Override
    public              String          toString() {
        switch (_type){
            case INT_CONST:
            case BOOL_CONST:
            case FLOAT_CONST:
                return "Константа '" + _char +"'";
            case VARIABLE:
                return "Переменная " + _id;
            case OPERATOR:
            case SPLITTER:
                return _char;
            case BLOCK_POINTER:
                return "Указатель на блок " + _char;
        }
        return "Нераспознанная лексема " + _id;
    }
}
