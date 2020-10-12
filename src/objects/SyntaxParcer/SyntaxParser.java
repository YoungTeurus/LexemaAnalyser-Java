package objects.SyntaxParcer;

import objects.LexemaParcer.Lexema;
import objects.SyntaxParcer.Rules.LeftRightRulesCombo;
import objects.SyntaxParcer.Rules.StaticRules;

import java.util.ArrayList;
import java.util.List;

import static objects.SyntaxParcer.Rules.StaticRules.leftRightRulesCombo_ForDefaultOperator;

public class SyntaxParser {

    public static class SyntaxParserOutput {
        public List<TreeNode> output_treenode_lexema_list;  // Список исходных лексем в виде TreeNode
        public List<List<List<TreeNode>>> expressions;  // Список выражений (состоящих из уровней)
        public List<TreeNode> expressions_trees;  // Список деревеьев для каждого из выражений
    }

    public static SyntaxParserOutput get_lexema_levels(List<Lexema> output_lexema_list) throws SyntaxParcerException {

        // Пытаемся понять поряд операций в строке
        // Каждой лексеме присваивается level её вложенности. После с помощью них будет создаваться дерево.
        // Удаляет все сплиттеры! (В данной реализации.)

        SyntaxParserOutput po = new SyntaxParserOutput();

        // Заменяем все Lexema на TreeNode для облегчения работы в следующем методе.
        List<TreeNode> output_treenode_lexema_list = new ArrayList<>();
        for (Lexema lex : output_lexema_list){
            output_treenode_lexema_list.add(new TreeNode().setContent(lex));
        }

        // Expression - отдельные выражения (разделённые ;)
        // |_ Levels - уровни внутри одного выражения
        //    |_ Level - уровень
        //       |_ TreeNode - дерево-лист
        //          |_.getContent() -> Lexema - лексема
        List<List<List<TreeNode>>> expressions = new ArrayList<>();
        expressions.add(new ArrayList<>());

        int current_expression = 0; // Текущее выражение

        List<List<TreeNode>> levels = expressions.get(current_expression);
        levels.add(new ArrayList<>());

        int current_level = 0; // Текущий уровень вложенности.
        //TODO: временно:
        int equals_added = 0;  // Количество добавленных знаков равно

        List<TreeNode> level = levels.get(current_level);


        //TODO: как-то вынести хардкод
        for (TreeNode node: output_treenode_lexema_list) {
            Lexema lex = node.getContent();
            if (lex.get_type() == Lexema.lexema_types.OPERATOR){
                if (lex.get_char().equals("=")){
                    // Особый случай "=" - создаём новый уровень, который выше текущего
                    List<TreeNode> new_upper_level = new ArrayList<>();
                    new_upper_level.add(node);
                    levels.add(current_level, new_upper_level);
                    equals_added += 1;
                    current_level += 1;
                    level = levels.get(current_level);
                }
                else {
                    // Каждый оператор заносим на текущий уровень
                    level.add(node);
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

        // Удаление всех сплиттеров (в текущей реализации).
        output_treenode_lexema_list.removeIf(treeNode -> treeNode.getContent().get_type() == Lexema.lexema_types.SPLITTER);

        po.output_treenode_lexema_list = output_treenode_lexema_list;
        po.expressions = expressions;

        return po;
    }

    public static List<TreeNode> get_tree(List<TreeNode> output_treenode_lexema_list,
                                          List<List<List<TreeNode>>> expressions,
                                          List<Lexema> output_lexema_list) throws SyntaxParcerException {
        // Строим дерево с помощью списка уровня лексем

        // Копия исходного листа, в котором будем хранить лексемы и TreeNode-ы
        List<TreeNode> expressions_trees = new ArrayList<>(); // Список, содержащий все TreeNode отдельных команд
        for (int current_expression = 0; current_expression < expressions.size(); current_expression++){
            // Разбираем по одному выражению
            List<List<TreeNode>> levels = expressions.get(current_expression);
            for (int current_level = levels.size() - 1; current_level >= 0; current_level--){
                // Начинаем с самого глубокого (большого по числу) уровня
                for(TreeNode node : levels.get(current_level)){
                    // Пусть все операции пока что унарные.
                    int node_i = output_treenode_lexema_list.indexOf(node); // Порядковый номер node

                    // Использует ли оператор элемент слева
                    Boolean use_left_neighbour = StaticRules.doesLexemaUsesLeftNeighbour.getOrDefault(
                            node.getContent().get_char(), true);

                    // Элементы возле оператора
                    TreeNode left_neighbour = null;
                    TreeNode right_neighbour;

                    if (use_left_neighbour){
                        // Пытаемся взять элемент слева:
                        try {
                            left_neighbour = output_treenode_lexema_list.get(node_i - 1);
                        }
                        catch (IndexOutOfBoundsException e){
                            throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Невозможно найти левый " +
                                    "элемент для оператора %s на позиции %d.", node.getContent().get_char(),
                                    output_lexema_list.indexOf(node.getContent())
                            )
                            );
                        }
                    }
                    // Пытаемся взять элемент справа:
                    try {
                        right_neighbour = output_treenode_lexema_list.get(node_i + 1);
                    }
                    catch (IndexOutOfBoundsException e){
                        throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Невозможно найти правый " +
                                "элемент для оператора %s на позиции %d.", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent()) ));
                    }

                    // Пытаемся найти правило для текущего оператора:
                    LeftRightRulesCombo rulesCombo =  StaticRules.stringLeftRightRulesComboMap.getOrDefault(
                            node.getContent().get_char(),
                            leftRightRulesCombo_ForDefaultOperator
                    );

                    if (use_left_neighbour){
                        if (rulesCombo.leftRule.check(left_neighbour)){
                            node.setLeft(left_neighbour);
                        }
                        else{
                            throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Левый элемент для оператора " +
                                    "%s на позиции %d не подходит под правило!", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent()) ));
                        }
                    }

                    if (rulesCombo.rightRule.check(right_neighbour)){
                        node.setRight(right_neighbour);
                    }
                    else{
                        throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Правый элемент для оператора " +
                                "%s на позиции %d не подходит под правило!", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent()) ));
                    }

                    // Удаление использованных лексем:
                    if (use_left_neighbour){
                        output_treenode_lexema_list.remove(left_neighbour);
                    }
                    output_treenode_lexema_list.remove(right_neighbour);

                    if (current_level == 0){
                        // Если дошли до верхнего уровня, добавляем текущее дерево на вывод
                        expressions_trees.add(node);
                    }
                }
            }
        }

        //TODO: сделать проверку на наличие деревьев, НЕ входящих в конечное выражение, что является ошибкой.

        // expression_trees содержит список деревьев разбора для каждого из выражений
        return expressions_trees;
    }
}
