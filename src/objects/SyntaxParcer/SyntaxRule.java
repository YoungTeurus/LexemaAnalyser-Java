package objects.SyntaxParcer;

import objects.LexemaParcer.Lexema;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SyntaxRule {
    public Lexema.lexema_types works_for; // Тип лексемы, для которой применяется это правило
    public String exact_char; // (Необязательно) для какой строковой лексемы предназначено данное правило

    public Function<Lexema, Boolean> left;  // Правило для проверки левой части выражения
    public Function<Lexema, Boolean> right; // Аналогично для правой части

    public SyntaxRule(){
        works_for = null;
        exact_char = null;
        left = null;
        right = null;
    }
}
