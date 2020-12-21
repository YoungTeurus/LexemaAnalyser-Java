package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.SyntaxParts.TreeNode;

/**
 * Правило для логической операции "ИЛИ" между двумя правилами.
 */
public class ORRule implements IRule {
    private final IRule rule1;
    private final IRule rule2;

    public ORRule(IRule rule1, IRule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    @Override
    public boolean check(TreeNode checking_node) {
        return rule1.check(checking_node) || rule2.check(checking_node);
    }
}