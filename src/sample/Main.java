package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.Parcer;
import objects.UniversalHashFunction_ForString;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        // launch(args);
        String str_lex = "a = b + c + a";
        List<String> str_lexes = Parcer.get_lexemas(str_lex);

        UniversalHashFunction_ForString hf = new UniversalHashFunction_ForString(33);

        for (String lex : str_lexes){
            System.out.println(hf.hash(lex));
        }
    }
}
