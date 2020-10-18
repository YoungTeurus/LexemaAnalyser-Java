package objects.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет одну команду кода, состоящую из метки, команды и аргумента(-ов).
 * Пример:
 * :LABEL CMD $1;
 * LABEL - метка (необязательно)
 * CMD - команда
 * $1 - аргумент (необязательно)
 *
 * Метка - строка, содержащая ":" в начале за которой следует буквенно-цифровая запись без пробелов.
 */
public class CodeExpression {
    private List<String> labels;
    private String command;
    private List<String> args;

    public CodeExpression(){
        labels = new ArrayList<>();
        command = "";
        args = new ArrayList<>();
    }

    public CodeExpression setCommand(String command) {
        this.command = command;
        return this;
    }

    public CodeExpression addLabel(String label){
        labels.add(label);
        return this;
    }

    public CodeExpression addArg(String arg) {
        args.add(arg);
        return this;
    }

    /**
     * Возвращает соответствующий команде код.
     * @return Строка типа :LABEL CMD $1;
     */
    public String toCode(){
        StringBuilder return_builder = new StringBuilder();
        for (String label: labels) {
            return_builder.append(label).append(" ");
        }
        return_builder.append(command);
        for (String arg : args){
            return_builder.append(" ").append(arg);
        }
        return_builder.append(";");
        return return_builder.toString();
    }

    @Override
    public String toString() {
        return "CodeExpression{" +
                "labels=" + labels +
                ", command='" + command + '\'' +
                ", args=" + args +
                '}';
    }
}
