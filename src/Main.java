import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.LexemaParcer.Lexema;
import objects.LexemaParcer.Parcer;
import objects.SyntaxParcer.SyntaxParcerException;
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
        String str = "a = (b + c) * 5 + d; x = abc + 3; a = not b;";
        Parcer.ParcerOutput lpo = Parcer.parce_string(str);
        System.out.println(lpo);

        try {
            objects.SyntaxParcer.Parcer.ParcerOutput spo = objects.SyntaxParcer.Parcer.get_lexema_levels(lpo.output_lexema_list);

            List<TreeNode> treeNodes;
            treeNodes = objects.SyntaxParcer.Parcer.get_tree(spo.output_treenode_lexema_list, spo.expressions, lpo.output_lexema_list);
            System.out.println(treeNodes);

        } catch (SyntaxParcerException e) {
            e.printStackTrace();
        }
    }
}
