package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import objects.Lexema;
import objects.Parcer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    protected TextArea textfield_input;

    @FXML
    private Button button_parse;

    @FXML
    private TableView<RowLexemaTableName> table_lexemas;

    @FXML
    private TextArea textfield_output;

    @FXML
    private TableView<RowHashTableName> table_hashtable;

    // private class EventHandlerParse implements EventHandler<MouseEvent>{
    //     @Override
    //     public void handle(MouseEvent event) {
    //         textfield_input
    //     }
    // }

    private void parce_input_string(MouseEvent event){
        String input_sting = textfield_input.getText();

        if (input_sting.length() == 0)
            return;

        Parcer.ParcerOutput po = Parcer.parce_string(input_sting);

        List<RowLexemaTableName> rowLexemaTableNameList = new ArrayList<>();
        for(Lexema lexema : po.object_lexema_list){
            rowLexemaTableNameList.add(
                    new RowLexemaTableName(
                        lexema.get_id(),
                        lexema.get_char(),
                        lexema.get_type().get_description()
                    )
            );
        }

        StringBuilder output_lexema_string = new StringBuilder();
        for (Lexema lexema : po.output_lexema_list){
            output_lexema_string.append(lexema.toString());
            output_lexema_string.append(" ");
        }

        textfield_output.setText(output_lexema_string.toString());

        List<RowHashTableName> rowHashTableNameList = new ArrayList<>();
        int i = 0;
        for(Object object : po.hashTable.get_table()){
            if (object != null){
                rowHashTableNameList.add(
                        new RowHashTableName(
                                i,
                                object.toString()
                        )
                );
            }
            i += 1;
        }

        table_lexemas.setItems(
                FXCollections.observableList(
                        rowLexemaTableNameList
                )
        );

        table_hashtable.setItems(FXCollections.observableList(rowHashTableNameList));

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Функция при инициализации.

        TableColumn<RowLexemaTableName, String> tc_lexemaId =
                new TableColumn<RowLexemaTableName, String>("ID лексемы");
        tc_lexemaId.setSortable(false);
        tc_lexemaId.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId()))
        );
        TableColumn<RowLexemaTableName, String> tc_lexemachar =
                new TableColumn<RowLexemaTableName, String>("Строковое представление");
        tc_lexemachar.setSortable(false);
        tc_lexemachar.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getString_reprecentation()))
        );
        TableColumn<RowLexemaTableName, String> tc_lexematype =
                new TableColumn<RowLexemaTableName, String>("Тип лексемы");
        tc_lexematype.setSortable(false);
        tc_lexematype.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getLexema_type()))
        );

        table_lexemas.getColumns().addAll(tc_lexemaId, tc_lexemachar, tc_lexematype);

        TableColumn<RowHashTableName, String> tc_hashId =
                new TableColumn<>("ID в хеш-таблице");
        tc_hashId.setSortable(false);
        tc_hashId.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId()))
        );

        TableColumn<RowHashTableName, String> tc_hashDescription =
                new TableColumn<>("ID в хеш-таблице");
        tc_hashDescription.setSortable(false);
        tc_hashDescription.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getString_reprecentation()))
        );

        table_hashtable.getColumns().addAll(tc_hashId, tc_hashDescription);

        button_parse.setOnMouseClicked(this::parce_input_string);

        // button_parse.setOnMouseClicked(event -> {
        //     textfield_input.setText("hello");
        // });
    }
}
