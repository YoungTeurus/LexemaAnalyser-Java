package objects.SyntaxParcer;

public class TreeNode {

    public Object getContent() {
        return content;
    }

    public TreeNode setLeft(Object left) {
        this.left = left;
        return this;
    }

    public TreeNode setRight(Object right) {
        this.right = right;
        return this;
    }

    public TreeNode setContent(Object content) {
        this.content = content;
        return this;
    }

    public Object getLeft() {
        return left;
    }

    public Object getRight() {
        return right;
    }

    private Object left;
    private Object right;
    private Object content;


    TreeNode(){
        left = null;
        right = null;
        content = null;
    }
}
