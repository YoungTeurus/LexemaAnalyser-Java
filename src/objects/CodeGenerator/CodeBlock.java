package objects.CodeGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Представляет блок кода, содержащий 0+ команд (строк кода).
 */
public class CodeBlock {
    private List<CodeExpression> expressions;

    CodeBlock(){
        expressions = new ArrayList<>();
    }

    public void addExpression(CodeExpression expression){
        expressions.add(expression);
    }

    public void addExpression(int index, CodeExpression expression){
        expressions.add(index, expression);
    }

    public void addExpressions(Collection<CodeExpression> expressionCollection){
        expressions.addAll(expressionCollection);
    }

    public void addExpressions(int index, Collection<CodeExpression> expressionCollection){
        expressions.addAll(index, expressionCollection);
    }

    public List<CodeExpression> getExpressions(){
        return expressions;
    }

    public CodeExpression get(int index){
        return expressions.get(index);
    }

    public int size(){
        return expressions.size();
    }

    /**
     * Возвращает весь код блока в виде выражений типа:
     * :LABEL CMD $0;
     * :LABEL1 CMD1 $1;
     * ...
     * @return Строка с переносами строк.
     */
    public String toCode(){
        return toCode(true);
    }

    public String toCode(boolean addNewLines){
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (CodeExpression codeExpression : expressions){
            stringBuilder.append(codeExpression.toCode());
            if (addNewLines) {
                if (i < expressions.size() - 1)
                    stringBuilder.append("\n");
            }
            i++;
        }
        return stringBuilder.toString();
    }
}
