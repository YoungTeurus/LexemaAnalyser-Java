// package objects.SyntaxParcer;
//
// import objects.LexemaParcer.Lexema;
//
// import java.lang.reflect.Type;
// import java.util.List;
//
// public class SyntaxRule {
//     public Lexema.lexema_types works_for; // Тип лексемы, для которой применяется это правило
//     public String exact_char; // (Необязательно) для какой строковой лексемы предназначено данное правило
//
//     public List<Type> allowed_left;  // Допустимые элементы слева
//     public List<Object> allowed_right; // Допустимые элементы справа
//
//     public SyntaxRule(){
//         works_for = null;
//         exact_char = null;
//         allowed_left = null;
//         allowed_right = null;
//     }
//
//     public SyntaxRule add_left(Type allowed_type){
//
//     }
//
//     public boolean is_compatible_with_lexema(Lexema lexema){
//         // Если не совпадает тип лексемы
//         if (lexema.get_type() != works_for)
//             return false;
//         // Если не совпадает char, для которого предназначено правило
//         return exact_char == null || lexema.get_char().equals(exact_char);
//         // Иначе правило подходит
//     }
//
//     public boolean check_left(Lexema lexema, Object left){
//         for (Type allowed_type : allowed_left){
//             if (left instanceof allowed_type)
//                 return true;
//         }
//         return true;
//     }
// }
