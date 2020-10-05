package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.TreeNode;

public interface IRule {
    boolean check(TreeNode checking_node);
}
