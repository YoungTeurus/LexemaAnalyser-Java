package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.HashTable;
import objects.ListUniqieer;
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

        // UniversalHashFunction_ForString hf = new UniversalHashFunction_ForString(33);
        // HashTable ht = new HashTable(33, hf);

        // for (String lex : str_lexes){
        //     System.out.println(ht.insert(lex, null));
        // }

        // for (String lex : str_lexes){
        //     System.out.println(ht.get_value(lex, null));
        // }

        str_lexes = new ListUniqieer<String>().unique_list(str_lexes);
        Parcer.ParcerOutput po = Parcer.get_object_lexema_list_and_str_object_lexema_hash_table(str_lexes);
        System.out.println(po);
    }
}
