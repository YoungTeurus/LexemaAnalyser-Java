package objects.SyntaxParcer.Rules;

import objects.LexemaParcer.Lexema;

import java.util.HashMap;
import java.util.Map;

public class StaticRules {
    // public static final IRule ruleForOperator = new TypeRule(Lexema.lexema_types.OPERATOR);
    // public static final IRule ruleForNotOperator = new NOTRule(ruleForOperator);
    public static final IRule ruleForEqualChar = new CharRule("=");
    public static final IRule ruleForNotEqualChar = new NOTRule(ruleForEqualChar);

    // Любая константа: целочисленная/вещественная/булева
    public static final IRule ruleForAnyConst = new ORRule(
            new ORRule(
                new TypeRule(Lexema.lexema_types.INT_CONST),
                new TypeRule(Lexema.lexema_types.BOOL_CONST)
            ),
            new TypeRule(Lexema.lexema_types.FLOAT_CONST)
    );

    public static final IRule ruleForVar = new TypeRule(Lexema.lexema_types.VARIABLE);

    // Любые данные:
    public static final IRule ruleForData = new ORRule(ruleForAnyConst, ruleForVar);


    // Проверка на принадлежность к знакам арифметических действий
    public static final IRule ruleForArithmeticSign = new ORRule(
            new ORRule(new CharRule("+"), new CharRule("-")),
            new ORRule(new CharRule("*"), new CharRule("/"))
            );

    // Проверка на принадлежность к логическим операторам
    public static final IRule ruleForBoolOperator = new ORRule(
            new ORRule(new CharRule("and"), new CharRule("or")),
            new ORRule(new CharRule("xor"), new CharRule("not"))
    );

    // Правила для конкретных проверок:

    // Проверка для соседних элементов арифметических знаков.
    // Верными соседями считаются: данные (константы, переменные), арифм. операции, булевы операции
    // public static final IRule ruleForArithmeticNeighbour = new ORRule(
    //         ruleForData,
    //         new ORRule(ruleForArithmeticSign, ruleForBoolOperator)
    // );
    public static final IRule ruleForArithmeticNeighbour = ruleForNotEqualChar;

    private static final LeftRightRulesCombo leftRightRulesCombo_ForArithmetic =
            new LeftRightRulesCombo(ruleForArithmeticNeighbour, ruleForArithmeticNeighbour);

    // Слева от знака равно - только переменная.
    public static final IRule ruleForEqualSignLeftNeighbour = ruleForVar;
    // Справа от знака равно - любой TreeNode.
    public static final IRule ruleForEqualSignRightNeighbour = new TrueRule();

    private static final LeftRightRulesCombo leftRightRulesCombo_ForEqualSign =
            new LeftRightRulesCombo(ruleForEqualSignLeftNeighbour,
            ruleForEqualSignRightNeighbour);

    // Для стандартного оператора слева и справа может быть что угодно, кроме знака равно.
    public static final IRule ruleForDefaultOperatorNeighbour = ruleForNotEqualChar;

    // Пара правил для стандартных операторов
    public static final LeftRightRulesCombo leftRightRulesCombo_ForDefaultOperator =
            new LeftRightRulesCombo(ruleForDefaultOperatorNeighbour, ruleForEqualSignRightNeighbour);

    // Словарь типа "строковое_представление, правила_для_левого_и_правого_соседа".
    public static final Map<String, LeftRightRulesCombo> stringLeftRightRulesComboMap = new HashMap<>();
    static {
        stringLeftRightRulesComboMap.put("=", leftRightRulesCombo_ForEqualSign);
        stringLeftRightRulesComboMap.put(":=", leftRightRulesCombo_ForEqualSign);
        stringLeftRightRulesComboMap.put("+",leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("-",leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("*",leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("/",leftRightRulesCombo_ForArithmetic);
    }

    // Словарь типа "строковое_представление, использует_ли_данный_оператор_левый_элемент"
    public static final Map<String, Boolean> doesLexemaUsesLeftNeighbour = new HashMap<>();
    static {
        doesLexemaUsesLeftNeighbour.put("not", false);
    }
}
