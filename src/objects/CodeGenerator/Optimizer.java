package objects.CodeGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Статический класс-оптимизатор кода.
 */
public class Optimizer {

    /**
     * Список коммутативных операций.
     */
    static List<String> commutativeOperations = new ArrayList<>(Arrays.asList(
            "ADD", "AND", "OR", "XOR"
    ));

    /**
     * Операторы в этом списке запрещают удаление кода между двумя LOAD-ами.
     * Обычно это операторы сохранения в переменную - STORE - и вывода на экран - OUT.
     */
    static List<String> importanceOperators = new ArrayList<>(Arrays.asList(
            "STORE", "OUT", "CMP"
    ));

    /**
     * Оптимизирует код, используя правила перестановки, перемещения и удаления строк.
     *
     * Правила:
     * 1) Последовательность опреаторов
     *      LOAD VAR0;
     *      CMD VAR1;
     * , где CMD - одна из коммутативных операций: ADD, SUB, MUL, DIV, AND, OR, XOR
     * может быть заменена на
     *      LOAD VAR1;
     *      CMD VAR0;
     * в случае, если CMD VAR1 больше не встречается в коде.
     *
     * 2) Последовательность операторов
     *      STORE VAR0;
     *      LOAD VAR0;
     * могут быть удалены, если VAR0 больше не будет использоваться, либо перед использованием VAR0 будет заполнена заново.
     * Упрощённое правило: может быть удалено LOAD VAR0, для этого достаточно, чтобы LOAD VAR0 не встречалось в других частях программы.
     *
     * 3) Последовательность операторов
     *      LOAD VAR0; (или IN VAR0;)
     *      STORE VAR1;
     * могут быть удалены, если за ними не следует другой оператор LOAD и нет перехода к оператору STORE VAR1, а последующие
     * VAR1 будут замены на VAR0 вполть до того места, где появляется другой оператор STORE VAR1 (исключая его).
     *
     * @param input_code Исходный код, полученный кодогенератором.
     * @return Полученный оптимизированный код.
     */
    public static CodeBlock optimize(CodeBlock input_code){

        // Провереряем каждую строку на соответствие правилу.
        // Делаем необходимые действия по правилу.

        ruleNumberOne(input_code);
        ruleNumberTwo(input_code);
        while(ruleNumberThree(input_code)){
        }
        while (true){
            ruleNumberOne(input_code);
            boolean a = ruleNumberTwo(input_code);
            boolean b = false;
            while(ruleNumberThree(input_code)){
                b = true;
            }
            if (!(a || b)){
                break;
            }
        }

        ruleNumberFour(input_code);
        ruleNumberFive(input_code);
        ruleNumberEight(input_code);

        return input_code;
    }

    /**
     * Сравнивает аргументы выражений one и another. Аргументы равны, если:
     * - их количество равно;
     * - каждый аргумент one равен соответствующему аргументу another.
     * @return True, если аргументы равны, иначе - false.
     */
    private static boolean compareArgs(CodeExpression one, CodeExpression another){
        if (one.getArgs().size() == another.getArgs().size()){
            // Если количество аргументов совпадает...
            int i = 0;
            for (String currentExpressionArg : one.getArgs()){
                if (!currentExpressionArg.equals(another.getArgs().get(i))){
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    /**
     * Проверяет, содержится ли аргумент arg в аргументах выражения codeExpression.
     * @return True, если один из аргументов codeExpression равен arg.
     */
    private static boolean containsArg(String arg, CodeExpression codeExpression){
        for(String _arg : codeExpression.getArgs()){
            if (_arg.equals(arg))
                return true;
        }
        return false;
    }

    /**
     * Проверяет, содержится ли один из аргуметов выражения src в аргументах выражеиня checkedExpression.
     * @return True, если один из аргументов src равен аргументу checkedExpression.
     */
    private static boolean containsArgsOf(CodeExpression src, CodeExpression checkedExpression){
        for (String arg : src.getArgs()){
            if (containsArg(arg, checkedExpression))
                return true;
        }
        return false;
    }

    /**
     * Меняет местами аргументы операторов one и another.
     */
    private static void swapArgs(CodeExpression one, CodeExpression another){
        List<String> temp = one.getArgs();
        one.setArgs(another.getArgs());
        another.setArgs(temp);
    }

    /**
     * Заменяет аргумент old_arg в выражении expression на аргумент new_arg.
     * Заменяет ВСЕ old_arg в выражении, если их больше 1.
     * Не производит никаких действий, если данный аргумент не был найден.
     */
    private static void changeArg(CodeExpression expression, String old_arg, String new_arg){
        List<Integer> foundOldArgs = new ArrayList<>();
        int i =0;
        for (String arg : expression.getArgs()){
            if (arg.equals(old_arg)){
                foundOldArgs.add(i);
            }
            i++;
        }
        for (int iOfOldArg : foundOldArgs){
            expression.getArgs().set(iOfOldArg, new_arg);
        }
    }

    /**
     * Удаляет выражение из блока кода, перенося все связанные метки элементу ниже (при необходимости создавая пустой).
     * @param itemIndexToRemove Индекс элемента для удаления.
     */
    private static void removeExpression(List<CodeExpression> expressions, int itemIndexToRemove){
        if (itemIndexToRemove == expressions.size() - 1){
            // Если удаляемый элемент является первым, создаём после него (в конце) NOP элемент
            expressions.add(new CodeExpression().setCommand("NOP"));
        }
        CodeExpression expressionToRemove = expressions.get(itemIndexToRemove);
        // Добавляем метки к элементу, идущему после нашего элемента:
        expressions.get(itemIndexToRemove + 1).addLabels(expressionToRemove.getLabels());
        // Удаляем исходный элемент:
        expressions.remove(itemIndexToRemove);
    }

    /**
     * 1) Последовательность опреаторов
     *      LOAD VAR0;
     *      CMD VAR1;
     * , где CMD - одна из коммутативных операций: ADD, SUB, MUL, DIV, AND, OR, XOR
     * может быть заменена на
     *      LOAD VAR1;
     *      CMD VAR0;
     * в случае, если CMD VAR1 больше не встречается в коде.
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberOne(CodeBlock input_code){
        int i = 0;
        int size = input_code.size();
        boolean wasReplaced = false;
        for (CodeExpression currentExpression : input_code.getExpressions()){
            if (i < size - 1 && currentExpression.getCommand().equals("LOAD")){
                // Если текущая команда не последняя и является командой "LOAD"...
                CodeExpression nextExpression = input_code.get(i+1);
                if (!commutativeOperations.contains(nextExpression.getCommand())) {
                    // Если следующая операция не коммутативная - переходим к следующей строчке.
                    i++;
                    continue;
                }
                swapArgs(currentExpression, nextExpression);
                wasReplaced = true;
            }
            i++;
        }

        return wasReplaced;
    }

    /**
     * 2) Последовательность операторов
     *      STORE VAR0;
     *      LOAD VAR0;
     * могут быть удалены, если VAR0 больше не будет использоваться, либо перед использованием VAR0 будет заполнена заново.
     * Упрощённое правило: может быть удалено LOAD VAR0, для этого достаточно, чтобы LOAD VAR0 не встречалось в других частях программы.
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberTwo(CodeBlock input_code){
        int i = 0;
        int size = input_code.size();
        boolean wasDeleted = false;
        List<Integer> items_to_remove = new ArrayList<>(); // Список индексов элементов, которые должны быть удалены
        for (CodeExpression currentExpression : input_code.getExpressions()) {
            if (i < size - 1 && currentExpression.getCommand().equals("STORE")) {
                // Если текущая команда не последняя и является командой "STORE"...
                CodeExpression nextExpression = input_code.get(i + 1);
                if (!nextExpression.getCommand().equals("LOAD")) {
                    // Если следующая операция не "LOAD" - переходим к следующей строчке.
                    i++;
                    continue;
                }
                // Сравниваем аргументы:
                if (!compareArgs(currentExpression, nextExpression)){
                    // Если аргументы не совпадают, переходим к следующей строчке.
                    i++;
                    continue;
                }
                // Если аргументы совпали, помечаем LOAD для удаления (см. ниже)
                // Проверяем, используются ли аргументы STORE дальше по коду...
                boolean removeSTORE = true;
                for (int j = i + 2; j < size; j++){
                    if (containsArgsOf(currentExpression, input_code.get(j))){
                        // Если используется, то переходим к следующей строчке, не удаляя STORE.
                        removeSTORE = false;
                        break;
                    }
                }
                // Если не используются, помечаем STORE для удаления.
                if (removeSTORE)
                    items_to_remove.add(i);
                items_to_remove.add(i+1); // Удаление LOAD.
                wasDeleted = true;
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                removeExpression(input_code.getExpressions(), (int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }

    /**
     * 3) Последовательность операторов
     *      LOAD VAR0; (или IN VAR0;)
     *      STORE VAR1;
     * могут быть удалены, если за ними не следует другой оператор LOAD и нет перехода к оператору STORE VAR1, а последующие
     * VAR1 будут замены на VAR0 вполть до того места, где появляется другой оператор STORE VAR1 (исключая его).
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberThree(CodeBlock input_code){
        boolean wasDeleted = false;
        int i = 0;
        int size = input_code.size();

        List<Integer> items_to_remove = new ArrayList<>(); // Список индексов элементов, которые должны быть удалены
        for (CodeExpression currentExpression : input_code.getExpressions()) {
            if (i < size - 1 &&
                    //(
                            currentExpression.getCommand().equals("LOAD")
                    //                || currentExpression.getCommand().equals("IN")
                    //)
            ) {
                // Если текущая команда не последняя и является командой "STORE"...
                CodeExpression nextExpression = input_code.get(i + 1);
                if (!nextExpression.getCommand().equals("STORE")) {
                    // Если следующая операция не "STORE" - переходим к следующей строчке.
                    i++;
                    continue;
                }
                if (compareArgs(currentExpression, nextExpression)){
                    // Если аргументы совпадают, то это что-то странное - выходим.
                    // Это должно исправлять правило 2.
                    i++;
                    continue;
                }
                boolean deleteLOAD = true;
                if (i < size - 2 && !input_code.get(i+2).getCommand().equals("LOAD")){
                    // Если после первой команды STORE следует любая команда кроме LOAD -
                    // нужно удалять только первый STORE, сохраняя LOAD.
                    deleteLOAD = false;
                }
                // Если имеются последовательные LOAD и STORE с разными аргументами...
                // Ищем следующее использование аргумента STORE по коду:
                for (int j = i + 2; j < size; j++){
                    CodeExpression cur_expr = input_code.get(j);
                    if (containsArgsOf(nextExpression, cur_expr)){
                        // Если используется, то заменяем аргументы VAR1 на VAR0.
                        // Пока не наткнёмся на STORE с аргументом VAR1.
                        if (cur_expr.getCommand().equals("STORE") && containsArgsOf(nextExpression, cur_expr)){
                            break;
                        }
                        // Меняем VAR1 на VAR0:
                        changeArg(cur_expr, nextExpression.getArgs().get(0), currentExpression.getArgs().get(0));
                    }
                }
                if (deleteLOAD){
                    items_to_remove.add(i);
                }
                items_to_remove.add(i+1);
                wasDeleted = true;
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                removeExpression(input_code.getExpressions(), (int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }

    /**
     * 4) Оператор
     *      STORE VAR0;
     * может быть удалён, если за ним не следует хотя бы один оператор, использующий VAR0.
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberFour(CodeBlock input_code){
        int i = 0;
        int size = input_code.size();
        boolean wasDeleted = false;
        List<Integer> items_to_remove = new ArrayList<>(); // Список индексов элементов, которые должны быть удалены
        for (CodeExpression currentExpression : input_code.getExpressions()) {
            if (currentExpression.getCommand().equals("STORE")) {
                // Если текущая команда является командой "STORE"...
                boolean needToDeleteSTORE = true;
                for (int j = i + 1; j < size; j++){
                    CodeExpression cur_exp = input_code.get(j);
                    if (containsArgsOf(currentExpression, cur_exp)){
                        // Если нашли хотя бы одно выражение, содрежащее аргумент STORE - переходим к следующей строке
                        needToDeleteSTORE = false;
                        break;
                    }
                }
                if (needToDeleteSTORE) {
                    // Если не нашли ни одного использования STORE - удаляем его.
                    items_to_remove.add(i);
                    wasDeleted = true;
                }
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                removeExpression(input_code.getExpressions(), (int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }

    /**
     * 5) Последовательность операторов, начинающихся с
     *      LOAD VAR0;
     * и заканчивающаяся:
     *      LOAD VAR1;
     * может быть удалена (не включая LOAD VAR1;), если между LOAD-ами нет команды STORE или OUT.
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberFive(CodeBlock input_code){
        int i = 0;
        int size = input_code.size();
        boolean wasDeleted = false;
        List<Integer> items_to_remove = new ArrayList<>(); // Список индексов элементов, которые должны быть удалены
        for (CodeExpression currentExpression : input_code.getExpressions()) {
            if (currentExpression.getCommand().equals("LOAD")) {
                // Если текущая команда является командой "LOAD"...
                // Ищем следующую команду LOAD или STORE.
                // Если нашли STORE или OUT - пропускаем текущий LOAD и переходим к следующей строчке.
                // Если нашли LOAD - удаляем код между ним и первым LOAD.
                boolean needToDeleteLOADBLOCK = true;
                int j = i + 1;
                for (; j < size; j++){
                    CodeExpression cur_exp = input_code.get(j);
                    if (importanceOperators.contains(cur_exp.getCommand())){
                        needToDeleteLOADBLOCK = false;
                        break;
                    }
                    if (cur_exp.getCommand().equals("LOAD")){
                        break;
                    }
                }
                if (needToDeleteLOADBLOCK){
                    for(int i1 = i; i1 < j; i1++){
                        items_to_remove.add(i1);
                    }
                }
                wasDeleted = true;
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                removeExpression(input_code.getExpressions(), (int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }

    /**
     * 6) Оператор
     *      NOP;
     * может быть удалён, если после него есть любая команда.
     * @return Возвращает true, если была сделана хотя бы одна замена, иначе вернёт false.
     */
    private static boolean ruleNumberEight(CodeBlock input_code){
        int i = 0;
        int size = input_code.size();
        boolean wasDeleted = false;
        List<Integer> items_to_remove = new ArrayList<>(); // Список индексов элементов, которые должны быть удалены
        for (CodeExpression currentExpression : input_code.getExpressions()) {
            if (currentExpression.getCommand().equals("NOP") && i + 1 < size) {
                // Если текущая команда является командой "NOP" и за ней есть ещё одна команда.
                items_to_remove.add(i);
                wasDeleted = true;
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                removeExpression(input_code.getExpressions(), (int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }
}
