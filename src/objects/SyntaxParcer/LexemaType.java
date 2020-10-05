package objects.SyntaxParcer;

import objects.LexemaParcer.Lexema;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LexemaType {
    // public static enum numOfParams{
    //     UNARY(1),
    //     BINARY(2),
    //     TRIPLET(3);

    //     public int get_paramCount() {
    //         return _paramCount;
    //     }

    //     int _paramCount;

    //     private numOfParams(int paramCount){
    //         _paramCount = paramCount;
    //     }
    // }


    // Хеш-таблица "строковая - количество параметров"
    // Считается, что все лексемы, которые сюда не вошли, имеют два параметра.
    public static Map<String, Integer> lexemaParamCount = new HashMap<>();
    static {
        lexemaParamCount.put("not", 1);
    }
}
