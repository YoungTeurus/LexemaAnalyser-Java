package objects.SyntaxParcer;

import objects.LexemaParcer.Lexema;
import objects.SyntaxParcer.Rules.LeftRightRulesCombo;
import objects.SyntaxParcer.Rules.StaticRules;
import objects.SyntaxParcer.SyntaxParts.Block;
import objects.SyntaxParcer.SyntaxParts.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static objects.SyntaxParcer.Rules.StaticRules.*;

public class SyntaxParser {

    public static class SyntaxParserOutput {
        // Block.content - блок кода
        // |_ TreeNode - деревья
        public List<Block> blocks; // Список блоков по уровням (?)
    }

    public static SyntaxParserOutput get_lexema_levels(List<Lexema> output_lexema_list) throws SyntaxParcerException {

        // Пытаемся понять поряд операций в строке
        // Каждой лексеме присваивается level её вложенности. После с помощью них будет создаваться дерево.
        // Удаляет все сплиттеры после выполнения! (В данной реализации.)

        SyntaxParserOutput po = new SyntaxParserOutput();

        // Заменяем все Lexema на TreeNode для облегчения работы в следующем методе.
        List<TreeNode> output_treenode_lexema_list = new ArrayList<>();
        for (Lexema lex : output_lexema_list){
            output_treenode_lexema_list.add(new TreeNode().setContent(lex));
        }

        /*
          Класс для хранения состояния парсера. Сохраняет уровни вложенности на момент входа во внутренний блок.
         */
        class ParserState{
            Block current_block;
            int current_expression;
            int current_level;
            int equals_added;

            ParserState(Block current_block, int current_expression, int current_level, int equals_added){
                this.current_block = current_block;
                this.current_expression = current_expression;
                this.current_level = current_level;
                this.equals_added = equals_added;
            }
        }

        // Block.content - блок кода
        // |_ Expressions - выражения
        //  |_ Levels - уровни внутри одного выражения
        //     |_ Level - уровень
        //        |_ TreeNode - дерево-лист
        //           |_.getContent() -> Lexema - лексема
        int last_block_id = 0;
        Stack<ParserState> upper_states = new Stack<>();  // Стак вложенности блоков (для поддержки фигурных скобок и вложенности)
        ArrayList<Block> blocks = new ArrayList<>();  // Список всех блоков (для вывода)
        blocks.add(new Block(last_block_id));
        Block current_block = blocks.get(last_block_id);  // Текущий блок кода

        List<List<List<TreeNode>>> expressions = current_block.expressions;
        expressions.add(new ArrayList<>());

        final int start_var = 0;  // Начальное выражение, уровень вложенности и количество знаков "="

        int current_expression = start_var; // Текущее выражение

        List<List<TreeNode>> levels = expressions.get(current_expression);
        levels.add(new ArrayList<>());

        //TODO: временно?


        int current_level = start_var; // Текущий уровень вложенности.
        //TODO: временно:
        int equals_added = start_var;  // Количество добавленных знаков равно

        List<TreeNode> level = levels.get(current_level);


        //TODO: как-то вынести хардкод
        for (TreeNode node: output_treenode_lexema_list) {
            boolean add_node_to_block = true;
            Lexema lex = node.getContent();
            if (lex.get_type() == Lexema.lexema_types.OPERATOR){
                if (lex.get_char().equals("=") || lex.get_char().equals("!=")){
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
                add_node_to_block = false;
                switch (lex.get_char()) {
                    case "{":
                        // Создаём новый блок...
                        last_block_id++;
                        Block new_block = new Block(last_block_id);
                        blocks.add(new_block);
                        // Создаём новую лексему, которую ставим в конец текущего Block
                        Lexema block_lexema = new Lexema(String.format("BLOCK_%d", last_block_id), -1, Lexema.lexema_types.BLOCK_POINTER);
                        block_lexema._value = new_block;
                        TreeNode block_treeNode = new TreeNode().setContent(block_lexema);
                        current_block.content.add(block_treeNode);
                        // Меняем текущий Block, занося его в стек:
                        upper_states.push(
                                new ParserState(current_block, current_expression, current_level, equals_added)
                        );
                        current_block = new_block;
                        // Сбрасываем значения переменных:
                        current_expression = current_level = equals_added = start_var;
                        // Устанавливаем локальные переменные:
                        expressions = current_block.expressions;
                        expressions.add(new ArrayList<>());
                        levels = expressions.get(current_expression);
                        levels.add(new ArrayList<>());
                        level = levels.get(current_level);
                        break;
                    case "}":
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

                        // Если всё хорошо в текущем блоке, возвращаем блок из стека:
                        ParserState ps = upper_states.pop();
                        current_block = ps.current_block;
                        current_expression = ps.current_expression;
                        current_level = ps.current_level;
                        equals_added = ps.equals_added;
                        expressions = current_block.expressions;
                        levels = expressions.get(current_expression);
                        level = levels.get(current_level);
                        break;
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

            // Добалвяем Node в текущий блок
            if (add_node_to_block){
                current_block.content.add(node);
            }
        }

        // Проверяем, что закрыты все фигурные скобки:
        if (!upper_states.empty()){
            throw new SyntaxParcerException(
                    "Уровень вложенности блоков нарушен! Проверьте количество фигурных скобок!"
            );
        }

        // Удаление всех сплиттеров (в текущей реализации).
        output_treenode_lexema_list.removeIf(treeNode -> treeNode.getContent().get_type() == Lexema.lexema_types.SPLITTER);

        po.blocks = blocks;

        return po;
    }

    public static List<Block> get_tree(SyntaxParserOutput spo,
                                          List<Lexema> output_lexema_list) throws SyntaxParcerException {
        // Строим дерево с помощью списка уровня лексем
        for (Block current_block : spo.blocks){
            List<List<List<TreeNode>>> expressions = current_block.expressions;
            // Копия исходного листа, в котором будем хранить лексемы и TreeNode-ы
            for (List<List<TreeNode>> levels : expressions) {
                // Разбираем по одному выражению
                for (int current_level = levels.size() - 1; current_level >= 0; current_level--) {
                    // Начинаем с самого глубокого (большого по числу) уровня
                    for (TreeNode node : levels.get(current_level)) {
                        int node_i = current_block.content.indexOf(node); // Порядковый номер node

                        // Использует ли оператор элемент слева
                        Boolean use_left_neighbour = StaticRules.doesLexemaUsesLeftNeighbour.getOrDefault(
                                node.getContent().get_char(), defaultDoesLexemaUsesLeftNeighbour);

                        Boolean can_be_unary = canBeUnary.getOrDefault(node.getContent().get_char(), defaultCanBeUnary);

                        // Элементы возле оператора
                        TreeNode left_neighbour = null;
                        TreeNode right_neighbour;

                        if (use_left_neighbour) {
                            // Пытаемся взять элемент слева:
                            int left_neighbour_offset = StaticRules.leftNeighbourOffset.getOrDefault(
                                    node.getContent().get_char(), defaultLeftNeighbourOffset
                            );
                            try {
                                left_neighbour = current_block.content.get(node_i + left_neighbour_offset);
                            } catch (IndexOutOfBoundsException e) {
                                throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Невозможно найти левый " +
                                                "элемент для оператора %s на позиции %d.", node.getContent().get_char(),
                                        output_lexema_list.indexOf(node.getContent())
                                )
                                );
                            }
                        }
                        int right_neighbour_offset = StaticRules.rightNeighbourOffset.getOrDefault(
                                node.getContent().get_char(), defaultRightNeighbourOffset
                        );
                        // Пытаемся взять элемент справа:
                        try {
                            right_neighbour = current_block.content.get(node_i + right_neighbour_offset);
                        } catch (IndexOutOfBoundsException e) {
                            throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Невозможно найти правый " +
                                    "элемент для оператора %s на позиции %d.", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent())));
                        }

                        // Пытаемся найти правило для текущего оператора:
                        LeftRightRulesCombo rulesCombo = StaticRules.stringLeftRightRulesComboMap.getOrDefault(
                                node.getContent().get_char(),
                                leftRightRulesCombo_ForDefaultOperator
                        );

                        if (use_left_neighbour) {
                            if (rulesCombo.leftRule.check(left_neighbour)) {
                                node.setLeft(left_neighbour);
                            } else {
                                // Если оператор не может быть унарным, то возвращаем ошибку...
                                if (!can_be_unary){
                                    throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Левый элемент для оператора " +
                                            "%s на позиции %d не подходит под правило!", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent())));
                                }
                                else {
                                    // Иначе заносим в левый член null.
                                    node.setLeft(null);
                                    use_left_neighbour = false; // Устанавливаем флаг в состояние "не использовал левый член".
                                }
                            }
                        }

                        if (rulesCombo.rightRule.check(right_neighbour)) {
                            node.setRight(right_neighbour);
                        } else {
                            throw new SyntaxParcerException(String.format("Найдена ошибка при разборе! Правый элемент для оператора " +
                                    "%s на позиции %d не подходит под правило!", node.getContent().get_char(), output_lexema_list.indexOf(node.getContent())));
                        }

                        // Удаление использованных лексем:
                        if (use_left_neighbour) {
                            current_block.content.remove(left_neighbour);
                        }
                        current_block.content.remove(right_neighbour);

                    }
                }
            }
        }


        //TODO: сделать проверку на наличие деревьев, НЕ входящих в конечное выражение, что является ошибкой.
        for (Block block : spo.blocks){
            for (TreeNode treeNode : block.content){
                if (treeNode.is_leaf()){
                    throw new SyntaxParcerException("Найдены незадействованные лексемы! Проверьте исходный код!");
                }
            }
        }

        // blocks содержит список деревьев разбора для каждого из выражений
        return spo.blocks;
    }
}
