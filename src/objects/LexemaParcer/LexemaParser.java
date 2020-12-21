package objects.LexemaParcer;

import objects.Hashing.HashTable;
import objects.Hashing.UniversalHashFunction_ForString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexemaParser {
    public static class LexemaParserOutput {
        public List<Lexema> object_lexema_list;
        public HashTable hashTable;
        public List<Lexema> output_lexema_list;
    }

    private final static int DEFAULT_HASH_TABLE_SIZE = 255; // Длина хеш-таблицы

    private final static String regex_pattern = "-?\\d+(.\\d+)*|[:(){}=<>;+\\-\\*\\/]|[a-zA-Z0-9_]+";

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
    private static List<String> get_lexemas(String input_string){
        ArrayList<String> return_list = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex_pattern);
        Matcher matcher = pattern.matcher(input_string);

        while (matcher.find()){
            return_list.add(input_string.substring(matcher.start(), matcher.end()));
        }

        return return_list;
    }

    /**
     * Возвращает два объекта: список объектов-лексем и словарь типа "строковая лексема - объект-лексема".
     * @param unique_str_lexema_list Список уникальных строковых лексем
     * @return ParcerOutput с двумя заполненными полями: списком объектов-лексем и хеш-таблицей, содержащей объекты-лексемы.
     */
    private static LexemaParserOutput get_object_lexema_list_and_str_object_lexema_hash_table(List<String> unique_str_lexema_list){
        LexemaParserOutput lexemaParserOutput = new LexemaParserOutput();

        int hash_table_size = DEFAULT_HASH_TABLE_SIZE;
        UniversalHashFunction_ForString hash_function = new UniversalHashFunction_ForString(hash_table_size);
        HashTable hashTable = new HashTable(hash_table_size, hash_function);

        List<Lexema> object_lexema_list = new ArrayList<Lexema>();
        int i = 0;
        for (String str_lexema : unique_str_lexema_list){
            Lexema obj_lexema = new Lexema(str_lexema, i); // Создание объектов-лексем
            object_lexema_list.add(obj_lexema); // Список объектов-лексем
            hashTable.insert(str_lexema, obj_lexema); // Добавляем в хеш-таблицу лексемы.
            i += 1;
        }

        lexemaParserOutput.object_lexema_list = object_lexema_list;
        lexemaParserOutput.hashTable = hashTable;

        return lexemaParserOutput;
    }

    /**
     * Сопоставляет исходные строковые лексемы и объекты-лексемы, возвращая список лексем.
     * @param str_lexema_list Исходный список строковых лексем.
     * @param hash_table Хеш-таблица, содержащая объекты-лексемы.
     * @return Список объектов-лексем в пордяке их следования в str_lexema_list.
     */
    private static List<Lexema> get_output_lexema_list(List<String> str_lexema_list, HashTable hash_table){
        List<Lexema> output_lexema_list = new ArrayList<>();
        for (String str_lexema : str_lexema_list){
            output_lexema_list.add(
                    // Сопоставляем последовательность строковых лексем и лексем-объектов
                    new Lexema((Lexema)hash_table.get_value(str_lexema, new String_Object_LexemaComparator()))
            );
        }
        return output_lexema_list;
    }

    /**
     * Осуществляет лексический разбор переданной строки, разбирая её на отдельные лексемы.
     * @param input_string Строка для разбора.
     * @return Объект, содержащий список всех лексем, хеш-таблицу типа "строковая лексема - объект-лексема"
     *  и список представления исходной строки в виде последовательности лексем.
     */
    public static LexemaParserOutput parce_string(String input_string){
        // 0. Убрать все переносы строк
        input_string = LexemaParser.prepare_string(input_string);
        // 1. Найти последовательность строковых лексем
        List<String> str_lexema_list = LexemaParser.get_lexemas(input_string);
        // 2. Найти уникальные строковые лексемы
        List<String> unique_str_lexema_list = new ListUniqieer<String>().unique_list(str_lexema_list);
        // 3. Создаём список объектов-лексем
        // 4. Создаём хеш-таблицу объектов-лексем
        LexemaParserOutput po = LexemaParser.get_object_lexema_list_and_str_object_lexema_hash_table(unique_str_lexema_list);
        // 5. Получаем выходной список
        po.output_lexema_list = LexemaParser.get_output_lexema_list(str_lexema_list, po.hashTable);

        return po;
    }
}
