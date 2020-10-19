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
            "ADD", "SUB", "MUL", "DIV", "AND", "OR", "XOR"
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
     *      LOAD VAR0;
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
                input_code.getExpressions().remove((int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }

    /**
     * 3) Последовательность операторов
     *      LOAD VAR0;
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
            if (i < size - 1 && currentExpression.getCommand().equals("LOAD")) {
                // Если текущая команда не последняя и является командой "STORE"...
            }
            CodeExpression nextExpression = input_code.get(i + 1);
            if (!nextExpression.getCommand().equals("STORE")) {
                // Если следующая операция не "STORE" - переходим к следующей строчке.
                i++;
                continue;
            }
            if (i < size - 2 && input_code.get(i+1).getCommand().equals("STORE")){
                // Если после первой команды STORE следует вторая команда STORE - не можем удалить и переходим к следующей строчке.
                // TODO: Нет, на самом деле нужно удалять только первый STORE, сохраняя LOAD.
                i++;
                continue;
            }
            if (compareArgs(currentExpression, nextExpression)){
                // Если аргументы совпадают, то это что-то странное - выходим.
                // Это должно исправлять правило 2.
                i++;
                continue;
            }
            // Если имеются последовательные LOAD и STORE с разными аргументами...
            // Ищем следующее использование аргумента STORE по коду:
            List<Integer> itemsToContainVAR1 = new ArrayList<>();
            for (int j = i + 2; j < size; j++){
                if (containsArgsOf(nextExpression, input_code.get(j))){
                    // Если используется, то запоминаем эту строчку для последующей проверки...
                    // Пока не наткнёмся на STORE с таким же аргументом
                    if (input_code.get(j).getCommand().equals("STORE")){
                        break;
                    }
                    itemsToContainVAR1.add(j);
                }
            }
            i++;
        }

        if (wasDeleted){
            // Удаляем элементы, начиная с последнего (чтобы не ломались индексы).
            for (int j = items_to_remove.size() - 1; j >= 0; j--){
                input_code.getExpressions().remove((int)items_to_remove.get(j));
            }
        }

        return wasDeleted;
    }
}
