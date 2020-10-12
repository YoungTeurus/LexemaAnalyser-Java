package objects.SyntaxParcer.Rules;

import objects.SyntaxParcer.SyntaxParts.TreeNode;

public interface IRule {
    boolean check(TreeNode checking_node);
}
