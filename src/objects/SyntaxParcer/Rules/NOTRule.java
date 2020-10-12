package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.SyntaxParts.TreeNode;

/**
 * Правило для логической операции "НЕ" для одного правила.
 */
public class NOTRule implements IRule {
    private final IRule rule;

    public NOTRule(IRule rule){
        this.rule = rule;
    }

    @Override
    public boolean check(TreeNode checking_node) {
        return !rule.check(checking_node);
    }
}