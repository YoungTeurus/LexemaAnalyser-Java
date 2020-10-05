package objects.SyntaxParcer;

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

    private TreeNode left;
    private TreeNode right;
    private Lexema content;


    TreeNode(){
        left = null;
        right = null;
        content = null;
    }

    /**
     * Является ли дерево листом.
     */
    boolean is_leaf(){
        return left == null && right == null;
    }
}
