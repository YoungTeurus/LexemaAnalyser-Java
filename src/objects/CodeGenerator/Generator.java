package objects.CodeGenerator;

import com.sun.org.apache.bcel.internal.classfile.Code;
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
 * JL m     : if FLAG == -1 then JUMP c(m)                  - если флаг равен -1 (результат сравнения - меньше), совершает передачу управления строке, адрес (номер) которой равен значению в ячейке m
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

    public static CodeBlock generate_code(Block block){
        // Генерирует код для одного блока.
        if (block == null)
            return null;
        CodeBlock codeBlock = new CodeBlock();

        for (TreeNode treeNode : block.content){
            codeBlock.content.addAll(generate_code(treeNode).content);
        }

        codeBlock.content.set(0, ":BLOCK" + block.id + " " + codeBlock.content.get(0));
        int last_i = codeBlock.content.size() - 1;
        codeBlock.content.set(last_i, ":BLOCK" + block.id + "_end " + codeBlock.content.get(last_i));

        return codeBlock;
    }

    public static CodeBlock generate_code(TreeNode treeNode){
        // Гененрирует код для одного TreeNode.
        if (treeNode == null)
            return null;
        CodeBlock codeBlock = new CodeBlock();

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
                            codeBlock.content.add(String.format(
                                    "LOAD %s;", treeNode.getRight().getContent().get_value()
                            ));
                            codeBlock.content.add(String.format(
                                    "STORE %s;", treeNode.getLeft().getContent().get_value()
                            ));
                            codeBlock.content.addAll(0, right_block.content);
                            break;
                        }
                    case "+": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "ADD %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "-": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "SUB %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "*": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "MUL %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "/": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "DIV %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "AND": {
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "AND %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "OR":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "OR %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "XOR":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "XOR %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "NOT":{
                        // Присваиваю вершине _value = ${last_id}
                        treeNode.getContent()._value = String.format("$%d", last_id++);
                        // Код на вывод: LOAD left.value; ADD right.value; STORE top.value;
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getRight().getContent().get_value()
                        ));
                        codeBlock.content.add(
                                "NOT;"
                        );
                        codeBlock.content.add(String.format(
                                "STORE %s;", treeNode.getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        break;
                    }
                    // TODO: объекденить код условных операторов, вынеся ._value в Lexema
                    case "<": {
                        treeNode.getContent()._value = "JL";
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "CMP %s;", treeNode.getRight().getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case ">":{
                        treeNode.getContent()._value = "JG";
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "CMP %s;", treeNode.getRight().getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "==":{
                        treeNode.getContent()._value = "JE";
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "CMP %s;", treeNode.getRight().getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "!=":{
                        treeNode.getContent()._value = "JN";
                        codeBlock.content.add(String.format(
                                "LOAD %s;", treeNode.getLeft().getContent().get_value()
                        ));
                        codeBlock.content.add(String.format(
                                "CMP %s;", treeNode.getRight().getContent().get_value()
                        ));
                        // Дописываем код левой и правой частей в начало текущего кода:
                        codeBlock.content.addAll(0, right_block.content);
                        codeBlock.content.addAll(0, left_block.content);
                        break;
                    }
                    case "if":{
                        // Условие:
                        codeBlock.content.addAll(left_block.content);
                        // ._value IF-а - это количество строк условия + 1
                        treeNode.getContent()._value = codeBlock.content.size() + 1;
                        // Переход:
                        codeBlock.content.add(String.format(
                                "%s %s;",   treeNode.getLeft().getContent().get_value(),
                                            treeNode.getRight().getContent().get_char()
                        ));
                        codeBlock.content.add(String.format(
                                "LOAD %s_end;", treeNode.getRight().getContent().get_char()
                        ));
                        codeBlock.content.add(
                                "ADD 1;"
                        );
                        codeBlock.content.add(String.format(
                                "STORE $%s;", last_id
                        ));
                        codeBlock.content.add(String.format(
                                "JMP $%s;", last_id++
                        ));
                        codeBlock.content.addAll(right_block.content);
                        codeBlock.content.add(
                                "NOP;"
                        );

                        break;
                    }
                    case "else":{
                        for(int i=0; i < (int)treeNode.getLeft().getContent()._value; i++){
                            codeBlock.content.add(left_block.content.get(i));
                        }
                        codeBlock.content.addAll(right_block.content);
                        for(int i= (int)treeNode.getLeft().getContent()._value; i < left_block.content.size(); i++){
                            codeBlock.content.add(left_block.content.get(i));
                        }
                        break;
                    }
                }
                break;
        }

        return codeBlock;
    }

    public static void generate_code(List<Block> blocks){
        // Готовый исходный код:
        List<CodeBlock> Code = new ArrayList<>();

        Block main_block = blocks.get(0);
        for (TreeNode treeNode : main_block.content){
            Code.add(generate_code(treeNode));
            // String operator = treeNode.getContent().get_char();
            // CodeBlock new_codeBlock;
            // switch (operator){
            //     case "=":
            //         new_codeBlock = new CodeBlock();
            //         new_codeBlock.content.add(String.format(
            //                 "LOAD %s;", treeNode.getLeft().getContent().get_value()
            //         ));
            //         new_codeBlock.content.add(String.format(
            //                 "STORE %s;", treeNode.getRight().getContent().get_value()
            //         ));
            //         Code.add(new_codeBlock);
            //         break;
            //     case "+":
            //         new_codeBlock = new CodeBlock();
            //         new_codeBlock.content.add(String.format(
            //                 "LOAD %s;", treeNode.getLeft().getContent().get_value()
            //         ));
            //         new_codeBlock.content.add(String.format(
            //                 "STORE %s;", treeNode.getRight().getContent().get_value()
            //         ));
            //         break;
            // }
        }
        CodeBlock output_code = new CodeBlock();
        for (CodeBlock codeBlock : Code){
            output_code.content.addAll(codeBlock.content);
        }
        System.out.println(output_code);
    }
}
