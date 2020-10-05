import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.LexemaParcer.Lexema;
import objects.LexemaParcer.Parcer;
import objects.SyntaxParcer.SyntaxRule;
import objects.SyntaxParcer.TreeNode;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        // launch(args);
        String str = "a = (b + c) * a";
        Parcer.ParcerOutput po = Parcer.parce_string(str);
        System.out.println(po);

        List<List<List<Lexema>>> expressions = objects.SyntaxParcer.Parcer.get_lexema_levels(po.output_lexema_list);

        List<TreeNode> treeNodes = objects.SyntaxParcer.Parcer.get_tree(po.output_lexema_list, expressions);
        // System.out.println(levels.toString());
    }
}
