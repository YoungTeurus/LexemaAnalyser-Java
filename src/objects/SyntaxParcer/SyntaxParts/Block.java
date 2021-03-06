package objects.SyntaxParcer.SyntaxParts;

import java.util.ArrayList;
import java.util.List;

/**
 * Блок выполнения: является кодом в фигурных скобках.
 * Используется для содержания:
 * - главного списка деревьев;
 * - выполняемого кода условных переходов.
 * <p>
 * Блок не имеет никакого Value (в отличии от TreeNode).
 */
public class Block {
    //TODO: можно ли вынести это отсюда?
    public List<List<List<TreeNode>>> expressions; // Выражения в блоке, упорядоченные по вложенности.

    public List<TreeNode> content;  // Содержание блока - все TreeNode, которые входят в данный Block.
    public int id;  // Идентификатор блока

    public Block(int id) {
        expressions = new ArrayList<>();
        content = new ArrayList<>();
        this.id = id;
    }
}
