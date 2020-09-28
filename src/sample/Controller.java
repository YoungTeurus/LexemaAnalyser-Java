package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button btn_print;

    @FXML
    private TextField textfield;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Функция при инициализации.

        btn_print.setOnMouseClicked(event -> {
            textfield.setText("hello");
        });
    }
}
