package GUI;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import objects.CodeGenerator.Generator;
import objects.LexemaParcer.Lexema;
import objects.LexemaParcer.LexemaParser;
import objects.SyntaxParcer.SyntaxParser;
import objects.SyntaxParcer.SyntaxParcerException;

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
    private TextArea textfield_log;

    @FXML
    private TableView<RowHashTableName> table_hashtable;

    /**
     * Очищает окно логов.
     */
    private void clear_logs(){
        textfield_log.clear();
    }

    /**
     * Добавляет строку в конец логов.
     */
    private void append_logs(String str){
        textfield_log.appendText(str);
    }

    /**
     * Возвращает текст, записанный в поле для ввода исходного кода.
     */
    private String get_input_string(){
        String input_string = textfield_input.getText();
        if (input_string.length() == 0){
            append_logs("Исходный код пуст!");
            return null;
        }
        return input_string;
    }

    /**
     * Заполняет таблицы комплилятора и выходную строку.
     * @param lpo Выход лексического анализатора.
     */
    private void fill_compile_tables(LexemaParser.LexemaParserOutput lpo){
        List<RowLexemaTableName> rowLexemaTableNameList = new ArrayList<>();
        for(Lexema lexema : lpo.object_lexema_list){
            rowLexemaTableNameList.add(
                    new RowLexemaTableName(
                            lexema.get_id(),
                            lexema.get_char(),
                            lexema.get_type().get_description()
                    )
            );
        }

        StringBuilder output_lexema_string = new StringBuilder();
        for (Lexema lexema : lpo.output_lexema_list){
            output_lexema_string.append(lexema.toString());
            output_lexema_string.append(" ");
        }

        textfield_output.setText(output_lexema_string.toString());

        List<RowHashTableName> rowHashTableNameList = new ArrayList<>();
        int i = 0;
        for(Object object : lpo.hashTable.get_table()){
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

    /**
     * Последовательно выполняет:
     * 1) Получение исходного текста кода.
     * 2) Лексический анализ кода.
     * 3) Заполнение таблиц компилятора
     * 4) Синтаксический анализ кода.
     * TODO: 5) Вывод текста программы на ЯП Ассемблер
     */
    private void parce_input_code(){
        // Очистка логов и таблиц компилятора:
        clear_logs();

        // 1) Получение исходного текста кода.
        String input_string = get_input_string();
        if (input_string == null)
            return;
        // 2) Лексический анализ кода.
        // lpo - Lexema Parser Output
        LexemaParser.LexemaParserOutput lpo = LexemaParser.parce_string(input_string);

        // 3) Заполнение таблиц компилятора
        fill_compile_tables(lpo);

        // 4) Синтаксический анализ кода.
        // spo - Syxtax Parser Output
        SyntaxParser.SyntaxParserOutput spo;
        try {
            spo = SyntaxParser.get_lexema_levels(lpo.output_lexema_list);
            SyntaxParser.get_tree(spo, lpo.output_lexema_list);
            append_logs("Синтаксический анализ прошёл успешно!\n");

            // 5) Вывод текста программы на ЯП Ассемблер
            String output_code = Generator.generate_code(spo.blocks).toCode();
        }
        catch (SyntaxParcerException e){
            append_logs(e.toString() + "\n");
        }
    }

    /**
     * Обработчик события для нажатия кнопки "Распарсить".
     */
    private void ButtonParse_Click(MouseEvent event){
        parce_input_code();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Функция при инициализации.

        TableColumn<RowLexemaTableName, String> tc_lexemaId =
                new TableColumn<>("ID лексемы");
        tc_lexemaId.setSortable(false);
        tc_lexemaId.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getId()))
        );
        TableColumn<RowLexemaTableName, String> tc_lexemachar =
                new TableColumn<>("Строковое представление");
        tc_lexemachar.setSortable(false);
        tc_lexemachar.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getString_reprecentation()))
        );
        TableColumn<RowLexemaTableName, String> tc_lexematype =
                new TableColumn<>("Тип лексемы");
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

        button_parse.setOnMouseClicked(this::ButtonParse_Click);
    }
}
