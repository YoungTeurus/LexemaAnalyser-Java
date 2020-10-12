package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.SyntaxParts.TreeNode;

/**
 * Правило, которое всегда возвращает true.
 */
public class TrueRule implements IRule {
    @Override
    public boolean check(TreeNode checking_node) {
        return true;
    }
}
