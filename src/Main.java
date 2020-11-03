import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.CodeGenerator.CodeBlock;
import objects.CodeGenerator.Generator;
import objects.CodeGenerator.Optimizer;
import objects.LexemaParcer.LexemaParser;
import objects.SyntaxParcer.SyntaxParcerException;
import objects.SyntaxParcer.SyntaxParser;
import objects.SyntaxParcer.SyntaxParts.Block;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI/GUI.fxml"));
        primaryStage.setTitle("Парсер кода");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        // String input0 = "a = 5 - 10; b = - 5;";
        // String input = "a = 5; b = 10 + a; c = a * b";
        // String input2 = "a = x AND b AND 8 AND n; if (wr < 10) { dg = uy}";
        // LexemaParser.LexemaParserOutput lpo = LexemaParser.parce_string(input0);
        // SyntaxParser.SyntaxParserOutput spo;
        // try {
        //     spo = SyntaxParser.get_lexema_levels(lpo.output_lexema_list);
        //     List<Block> blocks = SyntaxParser.get_tree(spo, lpo.output_lexema_list);
        //     System.out.println(blocks.size());
        //     // CodeBlock cb = Generator.generate_code(blocks);
        //     // Optimizer.optimize(cb);
        //     // System.out.println(spo);
        // }
        // catch (SyntaxParcerException i){
        //     System.out.println(i.toString());
        // }
    }
}
