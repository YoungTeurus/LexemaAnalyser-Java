package objects.SyntaxParcer.SyntaxParts;

import objects.LexemaParcer.Lexema;

public class TreeNode {

    public Lexema getContent() {
        return content;
    }

    public TreeNode setLeft(TreeNode left) {
        this.left = left;
        return this;
    }

    public TreeNode setRight(TreeNode right) {
        this.right = right;
        return this;
    }

    public TreeNode setContent(Lexema content) {
        this.content = content;
        return this;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }

    public Object getLeftValue() {
        return left == null ? null : left.content._value;
    }

    public Object getRightValue() {
        return right == null ? null : right.content._value;
    }

    private TreeNode left;
    private TreeNode right;
    private Lexema content;


    public TreeNode() {
        left = null;
        right = null;
        content = null;
    }

    /**
     * Является ли дерево листом.
     */
    public boolean is_leaf() {
        return left == null && right == null;
    }

    @Override
    public String toString() {
        return ((left != null) ? left.toString() + " " : "") +
                content.toString() +
                ((right != null) ? " " + right.toString() : "");
    }
}
