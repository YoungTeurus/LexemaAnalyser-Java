package objects.CodeGenerator;

import objects.SyntaxParcer.SyntaxParts.Block;
import objects.SyntaxParcer.SyntaxParts.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Генератор кода на языке Ассемблера на основе обхода дерева, получившегося в результате синтаксического разбора
 * исходного текста программы.
 *
 * Операторы Ассемблера (из методички):
 *
 * "m" обозначает название ячейки памяти
 * LOAD m   : c(m) -> сумматор                              - загружает значение из ячейки m в сумматор
 * ADD m    : c(сумматор) + c(m) -> сумматор                - складывает значение из сумматора с значением в ячейке m и помещает результат в сумматор
 * SUB m    : c(сумматор) - c(m) -> сумматор                - вычитает из значения в сумматоре значение в ячейке m и помещает результат в сумматор
 * MPY m    : c(сумматор) * c(m) -> сумматор                - перемножает значение из сумматора с значением в ячейке m и помещает результат в сумматор
 * DIV m    : с(сумматор) / c(m) -> сумматор                - делит значение из сумматора на значение в ячейке m и помещает результат в сумматор
 * STORE m  : c(сумматор) -> m                              - записывает значение из сумматора в ячейку m
 * AND m    : c(сумматор) AND c(m) -> сумматор              - совершает побитовую операцию И над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * OR m     : c(сумматор) OR c(m) -> сумматор               - совершает побитовую операцию ИЛИ над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * XOR m    : c(сумматор) XOR c(m) -> сумматор              - совершает побитовую операцию ИСКЛЮЧАЮЩЕЕ ИЛИ над значением из сумматора и значением в ячейке m и помещает результат в сумматор
 * NOT      : NOT c(сумматор) -> сумматор                   - совершает побитовую операцию НЕ над значением в сумматоре и помещает результат в сумматор
 * CMP m    : if c(сумматор) == c(m) then FLAG = 0
 *            else c(сумматор) > c(m) then FLAG = 1
 *            else FLAG = -1
 * - сравнивает значения в сумматоре со значением в ячейке m, и в случае их равенства устанавливает флаг равным 0, если значение в сумматоре больше - равным 1, иначе - -1.
 * JE m     : if FLAG == 0 then JUMP c(m)                   - если флаг равен 0 (результат сравенения - равно), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JN m     : if FLAG != 0 then JUMP c(m)
 * JG m     : if FLAG == 1 then JUMP c(m)                   - если флаг равен 1 (результат сравнения - больше), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JS m     : if FLAG == -1 then JUMP c(m)                  - если флаг равен -1 (результат сравнения - меньше), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JMP m    : JUMP c(m)                                     - совершает безусловную передачу управления строке, адрес (номер) которой равен значению в ячейке m
 * JMP
 *
 * "=m" обозначает численное значение
 * LOAD =m  : m -> сумматор                                 - помещает значение m в сумматор
 * ADD =m   : c(сумматор) + m -> сумматор                   - складывает значение из сумматора с значением m и помещает результат в сумматор
 * SUB =m   : c(сумматор) - m -> сумматор
 * MPY =m   : c(сумматор) * m -> сумматор                   - перемножает значение из сумматора с значением m и помещает результат в сумматор
 * DIV =m    : с(сумматор) / m -> сумматор
 * AND =m   : c(сумматор) AND m -> сумматор
 * OR =m    : c(сумматор) OR m -> сумматор
 * XOR =m   : c(сумматор) XOR m -> сумматор
 * CMP =m   : if c(сумматор) == m then FLAG = 1 else FLAG = 0
 *
 * Операторы исходного языка и соотносящийся им код Ассемблера:
 * a + b        -> ... b; STORE $b; LOAD $a; ADD $b;
 * a * b        -> ... b; STORE $b; LOAD $a; MUL $b;
 * a - b        -> ... b; STORE $b; LOAD $a; SUB $b;
 * a / b        -> ... b; STORE $b; LOAD $a; DIV $b;
 * a AND b      -> ... b; STORE $b; LOAD $a; AND $b;
 * a OR b       -> ... b; STORE $b; LOAD $a; OR $b;
 * a XOR b      -> ... b; STORE $b; LOAD $a; XOR $b;
 * NOT a        -> ... a; LOAD $a; NOT;
 */
public class Generator {
    private static int last_id = 0;

    /**
     * Гененрирует код для одного TreeNode, гененрируя код для каждого TreeNode, входящего в него.
     * Добавляет метки начала и конца блока.
     * @param block Block, для которого генерируется код.
     * @see Block
     * @return CodeBlock, соответствующий коду для данного block.
     */
    public static CodeBlock generate_code(Block block){
        // Генерирует код для одного блока.
        if (block == null)
            return null;
        CodeBlock codeBlock = new CodeBlock();

        for (TreeNode treeNode : block.content){
            codeBlock.addExpressions(generate_code(treeNode).getExpressions());
        }

        if (codeBlock.size() == 0){
            codeBlock.addExpression(new CodeExpression().setCommand("NOP"));
        }
        
        // Добавляем первому и последнему выржаению метки блока:
        codeBlock.get(0).addLabel(":BLOCK" + block.id);
        int last_i = codeBlock.size() - 1;
        codeBlock.get(last_i).addLabel(":BLOCK" + block.id + "_end");

        return codeBlock;
    }

    /**
     * Генерирует код для одного TreeNode, рекурсивно генерируя код для его правого и левого листа, если они есть.
     * @param treeNode TreeNode, для которого необходимо сгенерировать код.
     * @return CodeBlock, соответствующий коду для данного treeNode.
     */
    public static CodeBlock generate_code(TreeNode treeNode){
        if (treeNode == null)
            return null;
        CodeBlock codeBlock = new CodeBlock();

        // Получаем код левого и правых листьев.
        CodeBlock left_block = generate_code(treeNode.getLeft());
        CodeBlock right_block = generate_code(treeNode.getRight());

        // Генерируемый код зависит от типа оператора:
        switch (treeNode.getContent().get_type()) {
            case BLOCK_POINTER:
                codeBlock = generate_code((Block) treeNode.getContent()._value);
                break;
            case OPERATOR:
                switch (treeNode.getContent().get_char()) {
                    case ":=":
                    case "=": {
                        // Добавление команды LOAD с аргументом в виде правого соседа:
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        break;
                        }
                    case "+": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("ADD")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "-": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; SUB right.value; STORE top.value;
                        // Если знак унарный, то вместо left.value нужно подставить 0.
                        if (treeNode.getLeft() == null){
                            codeBlock.addExpression(
                                    new CodeExpression()
                                            .setCommand("LOAD")
                                            .addArg("0")
                            );
                        }
                        else{
                            codeBlock.addExpression(
                                    new CodeExpression()
                                            .setCommand("LOAD")
                                            .addArg(treeNode.getLeftValue().toString())
                            );
                        }
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("SUB")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        if (treeNode.getLeft() != null) {
                            codeBlock.addExpressions(0, left_block.getExpressions());
                        }
                        break;
                    }
                    case "*": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("MUL")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "/": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("DIV")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "AND": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("AND")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "OR":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("OR")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "XOR":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("XOR")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "NOT":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD right.value; NOT; STORE top.value;
                        codeBlock.addExpression(
                                    new CodeExpression()
                                            .setCommand("LOAD")
                                            .addArg(treeNode.getRightValue().toString())
                            );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("NOT")
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getContent().get_value().toString())
                        );
                        // Дописываем код правой части в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        break;
                    }
                    // TODO: объеденить код условных операторов, вынеся ._value в Lexema
                    case "<": {
                        treeNode.getContent()._value = "JS";
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("CMP")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case ">":{
                        treeNode.getContent()._value = "JG";
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("CMP")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "==":{
                        treeNode.getContent()._value = "JE";
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("CMP")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "!=":{
                        treeNode.getContent()._value = "JN";
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getLeftValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("CMP")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        codeBlock.addExpressions(0, left_block.getExpressions());
                        break;
                    }
                    case "IF":{
                        // Условие:
                        codeBlock.addExpressions(left_block.getExpressions());
                        // ._value IF-а - это количество строк условия + 1
                        treeNode.getContent()._value = codeBlock.size() + 1;
                        // Переход:
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand(treeNode.getLeftValue().toString())
                                        .addArg(treeNode.getRight().getContent().get_char())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getRight().getContent().get_char() + "_end")
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("ADD")
                                        .addArg("1")
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg("$" + last_id)
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("JMP")
                                        .addArg("$" + last_id++)
                        );
                        codeBlock.addExpressions(right_block.getExpressions());
                        codeBlock.addExpression(new CodeExpression().setCommand("NOP"));
                        break;
                    }
                    case "ELSE":{
                        for(int i=0; i < (int)treeNode.getLeft().getContent()._value; i++){
                            codeBlock.addExpression(left_block.get(i));
                        }
                        codeBlock.addExpressions(right_block.getExpressions());
                        for(int i= (int)treeNode.getLeft().getContent()._value; i < left_block.size(); i++){
                            codeBlock.addExpression(left_block.get(i));
                        }
                        break;
                    }
                    case "IN":{
                        // Код на вывод: IN; STORE right.value;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("IN")
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("STORE")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        // Дописываем код правой части в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        break;
                    }
                    case "OUT":{
                        // Код на вывод: LOAD right.value; OUT;
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("LOAD")
                                        .addArg(treeNode.getRightValue().toString())
                        );
                        codeBlock.addExpression(
                                new CodeExpression()
                                        .setCommand("OUT")
                        );
                        // Дописываем код правой части в начало текущего кода:
                        codeBlock.addExpressions(0, right_block.getExpressions());
                        break;
                    }
                }
                break;
        }

        return codeBlock;
    }

    /**
     * Генерирует код из блоков кода дерева разбора.
     * @param blocks Блоки кода, полученные на этапе синтаксического разбора.
     * @return CodeBlock, содеражащий код в виде CodeExpressions.
     */
    public static CodeBlock generate_code(List<Block> blocks){
        // Готовый исходный код:
        last_id = 0;
        List<CodeBlock> Code = new ArrayList<>();

        Block main_block = blocks.get(0);
        for (TreeNode treeNode : main_block.content){
            Code.add(generate_code(treeNode));
        }
        CodeBlock output_code = new CodeBlock();
        for (CodeBlock codeBlock : Code){
            output_code.addExpressions(codeBlock.getExpressions());
        }

        return output_code;
    }
}
