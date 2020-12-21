package objects.SyntaxParcer.Rules;

import objects.LexemaParcer.Lexema;
import objects.SyntaxParcer.SyntaxParts.TreeNode;

/**
 * Правило для проверки вершины TreeNode на содержание определённого типа лексемы.
 */
public class TypeRule implements IRule {
    private final Lexema.lexema_types checking_type;

    public TypeRule(Lexema.lexema_types checking_type) {
        this.checking_type = checking_type;
    }

    @Override
    public boolean check(TreeNode checking_node) {
        return ((Lexema) checking_node.getContent()).get_type() == checking_type;
    }
}
