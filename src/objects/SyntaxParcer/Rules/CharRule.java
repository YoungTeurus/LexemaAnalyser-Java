package objects.SyntaxParcer.Rules;

import objects.LexemaParcer.Lexema;
import objects.SyntaxParcer.SyntaxParts.TreeNode;

/**
 * Правило для проверки вершины TreeNode на содержание определённой строковой лексемы.
 */
public class CharRule implements IRule {
    private final String checking_char;
    public CharRule(String checking_char){
        this.checking_char = checking_char;
    }

    @Override
    public boolean check(TreeNode checking_node) {
        return ((Lexema)checking_node.getContent()).get_char().equals(checking_char);
    }
}
