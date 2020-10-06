package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.TreeNode;

/**
 * Правило для проверки: явлется ли TreeNode листом (то есть, есть ли у него в left и в right что либо).
 */
public class LeafRule implements IRule {
    @Override
    public boolean check(TreeNode checking_node) {
        return (checking_node.getLeft() == null && checking_node.getRight() == null);
    }
}
