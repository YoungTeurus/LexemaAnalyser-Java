package objects.SyntaxParcer;

import objects.LexemaParcer.Lexema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parcer {

    private static class SyntaxLexema{
        public Lexema _lexema;
        public int _pos;

        SyntaxLexema(Lexema lexema, int pos){
            _lexema = lexema;
            _pos = pos;
        }
    }

    public static List<List<List<Lexema>>> get_lexema_levels(List<Lexema> output_lexema_list) throws SyntaxParcerException {
        // Пытаемся понять поряд операций в строке
        // Каждой лексеме присваивается level её вложенности. После с помощью них будет создаваться дерево.
        // Удаляет все сплиттеры! (В данной реализации.)

        // Expression - отдельные выражения (разделённые ;)
        // |_ Levels - уровни внутри одного выражения
        //    |_ Level - уровень
        //       |_ Lexema - лексема
        List<List<List<Lexema>>> expressions = new ArrayList<>();
        expressions.add(new ArrayList<>());

        int current_expression = 0; // Текущее выражение

        List<List<Lexema>> levels = expressions.get(current_expression);
        levels.add(new ArrayList<>());

        int current_level = 0; // Текущий уровень вложенности.
        //TODO: временно:
        int equals_added = 0;  // Количество добавленных знаков равно

        List<Lexema> level = levels.get(current_level);


        //TODO: как-то вынести хардкод
        for (Lexema lex: output_lexema_list) {
            if (lex.get_type() == Lexema.lexema_types.OPERATOR){
                if (lex.get_char().equals("=")){
                    // Особый случай "=" - создаём новый уровень, который выше текущего
                    List<Lexema> new_upper_level = new ArrayList<>();
                    new_upper_level.add(lex);
                    levels.add(current_level, new_upper_level);
                    equals_added += 1;
                    current_level += 1;
                    level = levels.get(current_level);
                }
                else {
                    // Каждый оператор заносим на текущий уровень
                    level.add(lex);
                }
            }
            else if (lex.get_type() == Lexema.lexema_types.SPLITTER){
                switch (lex.get_char()) {
                    case "(":
                        // Каждая открывающая скобка повышает уровень вложенности
                        current_level += 1;
                        // Если этого уровня ещё нет...
                        if (levels.size() <= current_level) {
                            levels.add(new ArrayList<>());
                        }
                        level = levels.get(current_level);
                        break;
                    case ")":
                        // Каждая закрывающая скобка понижает уровень вложенности
                        current_level -= 1;
                        level = levels.get(current_level);
                        break;
                    case ";":
                        // Сбрасываем уровень, если встретили ";"

                        // Проверка на правильное количество открывающих и закрывающих скобок:
                        if (current_level != equals_added){
                            // Если к концу выражения мы не находимся на стандартном уровне - что-то не так с вложенностью
                            throw new SyntaxParcerException(
                                    String.format(
                                            "Уровень вложенности нарушен: имеем %d, ожидался %d",
                                            current_level, equals_added
                                    )
                            );
                        }

                        current_expression += 1;
                        // Добавляем новое выражение...
                        expressions.add(new ArrayList<>());
                        // Создаём новые уровни...
                        levels = expressions.get(current_expression);
                        levels.add(new ArrayList<>());
                        current_level = 0;
                        equals_added = 0;
                        // Переводим указатели на правильные уровни
                        level = levels.get(current_level);
                        break;
                }
            }
        }

        // TODO: подумать, как убирать сплиттеры из дерева разбора

        // Удаление всех сплиттеров (в текущей реализации).
        // output_lexema_list.removeIf(lexema -> lexema.get_type() == Lexema.lexema_types.SPLITTER);

        // TODO: Вынести всю логику в другой класс(-ы), чтобы уйти от статических правил для различных лексем.
        // for (rule in Rules): if rule.type == lex.type: rule.do_stuff(current_node, lex)

        return expressions;
    }

    public static List<TreeNode> get_tree(List<Lexema> output_lexema_list, List<List<List<Lexema>>> expressions) throws SyntaxParcerException {
        // Строим дерево с помощью списка уровня лексем

        // Копия исходного листа, в котором будем хранить лексемы и TreeNode-ы
        List<TreeNode> expressions_trees = new ArrayList<>(); // Список, содержащий все TreeNode отдельных команд
        List<Object> output_lexema_list_copy = new ArrayList<>(output_lexema_list);
        for (int current_expression = 0; current_expression < expressions.size(); current_expression++){
            // Разбираем по одному выражению
            List<List<Lexema>> levels = expressions.get(current_expression);
            for (int current_level = levels.size() - 1; current_level >= 0; current_level--){
                // Начинаем с самого глубокого (большого по числу) уровня
                for(Lexema lex : levels.get(current_level)){
                    // Пусть все операции пока что унарные.
                    int lex_i = output_lexema_list_copy.indexOf(lex); // Порядковый номер лексемы
                    TreeNode tree = new TreeNode().setContent(lex);

                    // Ошибка может произойти здесь, если какого-нибудь элемента не будет слева или справа
                    // При этом для разных операторов, например, not отсутствие левого элемента не будет ошибкой.

                    // Заполнение дерева:
                    try {
                        tree.setLeft(output_lexema_list_copy.get(lex_i - 1));
                        tree.setRight(output_lexema_list_copy.get(lex_i + 1));
                    }
                    catch (IndexOutOfBoundsException e){
                        throw new SyntaxParcerException("Найдена ошибка при разборе!");
                    }

                    // Удаление использованных лексем:
                    output_lexema_list_copy.add(lex_i, tree);  // Добавляю Treenode на место опреатора
                    output_lexema_list_copy.remove(tree.getContent());  // Удаляю оператор
                    output_lexema_list_copy.remove(tree.getLeft());  // Удаляю левый член
                    output_lexema_list_copy.remove(tree.getRight());  // Удаляю правый член

                    if (current_level == 0){
                        // Если дошли до верхнего уровня, добавляем текущее дерево на вывод
                        expressions_trees.add(tree);
                    }
                }
            }
        }

        // expression_trees содержит список деревьев разбора для каждого из выражений
        return expressions_trees;
    }
}
