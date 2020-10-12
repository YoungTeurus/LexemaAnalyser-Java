import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.LexemaParcer.LexemaParser;
import objects.SyntaxParcer.SyntaxParcerException;
import objects.SyntaxParcer.SyntaxParser;

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
        // launch(args);
        String input = "a=10; b=15; if (a>b) { c = a; } else { c=b;}";
        LexemaParser.LexemaParserOutput lpo = LexemaParser.parce_string(input);
        SyntaxParser.SyntaxParserOutput spo;
        try {
            spo = SyntaxParser.get_lexema_levels(lpo.output_lexema_list);
        }
        catch (SyntaxParcerException ignored){
        }
    }
}
