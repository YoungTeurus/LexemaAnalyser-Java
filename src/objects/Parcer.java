package objects;

import java.util.ArrayList;
import java.util.List;

public class Parcer {
    /**
     * Метод подготавливает исходную строку для прохода парсера.
     * Убирает все переносы строк.
     * @param input_string Исходная строка.
     * @return Изменённая строка.
     */
    private static String prepare_string(String input_string){
        String return_string;
        return_string = input_string.replace("\n", "");
        // Заготовка для расширения.
        return return_string;
    }

    /**
     * Проходит про строке и разбивает её на лексемы. Лексемы могут повторяться.
     * См. splitters в Lexema.
     * @param input_string Строка для прохода.
     * @return Список лексем в порядке, как в строке.
     */
    public static List<String> get_lexemas(String input_string){
        ArrayList<String> return_list = new ArrayList<>();

        int string_len = input_string.length();
        int lex_start = 0;  // Индекс начала лексемы
        int next_char_i;  // Индекс следующего символа в строке
        char next_char;  // Следующий символ в строке
        StringBuilder current_lexema;  // Текущая лексема

        while (lex_start < string_len){
            boolean not_splitter_lexema = true;  // Является ли текущая лексема сплиттером
            current_lexema = new StringBuilder(String.valueOf(input_string.charAt(lex_start)));
            // Если встретили отдельный пробел, просто пропускаем его:
            if (current_lexema.toString().equals(" ")){
                lex_start += 1;
                continue;
            }
            // Если новая лексема начинается с символа, который есть у опреатора:
            if (Lexema.is_start_of_any_reserved_lexema(current_lexema.toString())){
                // Начинаем искать конец сплиттера
                next_char_i = lex_start + 1;
                while (next_char_i < string_len){
                    next_char = input_string.charAt(next_char_i);
                    if (Lexema.is_start_of_any_reserved_lexema(current_lexema.toString() + next_char)){
                        current_lexema.append(next_char);
                        next_char_i += 1;
                        lex_start += 1;
                    }
                    else{
                        if (Lexema.is_any_reserved_lexema(current_lexema.toString())){
                            // Если уже составили сплиттер, прекращаем обход
                            not_splitter_lexema = false;
                        }
                        // Если лексема-сплиттер прерывается, начинаем обрабатывать её как обычную лексему
                        break;
                    }
                }
            }
            if (not_splitter_lexema){
                next_char_i = lex_start + 1;
                while (next_char_i < string_len){
                    next_char = input_string.charAt(next_char_i);
                    // Пока не нашли сплиттер:
                    if (Lexema.splitters.contains(String.valueOf(next_char))){
                        break;
                    }
                    current_lexema.append(next_char);
                    next_char_i += 1;
                    lex_start += 1;
                }
            }
            // Когда дошли до конца лексемы
            lex_start += 1;
            return_list.add(current_lexema.toString());
        }

        return return_list;
    }
}
