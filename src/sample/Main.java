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
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        // String str = "a = b + c + a";
        // Parcer.ParcerOutput po = Parcer.parce_string(str);
        // System.out.println(po);
    }
}
