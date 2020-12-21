package objects.SyntaxParcer.Rules;

import objects.LexemaParcer.Lexema;

import java.util.HashMap;
import java.util.Map;

public class StaticRules {
    public static final IRule ruleForEqualChar = new CharRule("=");
    public static final IRule ruleForNotEqualChar = new NOTRule(ruleForEqualChar);

    public static final IRule ruleForIfOperator = new ANDRule(
            new CharRule("IF"),
            new TypeRule(Lexema.lexema_types.OPERATOR)
    );

    public static final IRule ruleForBlockPointer = new TypeRule(Lexema.lexema_types.BLOCK_POINTER);

    public static final IRule ruleForNotLeaf = new NOTRule(new LeafRule());

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
            new ORRule(new CharRule("AND"), new CharRule("OR")),
            new ORRule(new CharRule("XOR"), new CharRule("NOT"))
    );

    // Правила для конкретных проверок:

    // Проверка для соседних элементов арифметических знаков.
    // Верными соседями считаются: данные (константы, переменные) ИЛИ арифм. операции, булевы операции, не являющиеся листами
    public static final IRule ruleForArithmeticNeighbour = new ORRule(
            ruleForData,
            new ANDRule(new ORRule(ruleForArithmeticSign, ruleForBoolOperator), ruleForNotLeaf)
    );

    private static final LeftRightRulesCombo leftRightRulesCombo_ForArithmetic =
            new LeftRightRulesCombo(ruleForArithmeticNeighbour, ruleForArithmeticNeighbour);

    // Слева от знака равно - только переменная.
    public static final IRule ruleForEqualSignLeftNeighbour = ruleForVar;
    // Справа от знака равно - любой TreeNode.
    public static final IRule ruleForEqualSignRightNeighbour = new TrueRule();

    private static final LeftRightRulesCombo leftRightRulesCombo_ForEqualSign =
            new LeftRightRulesCombo(ruleForEqualSignLeftNeighbour,
                    ruleForEqualSignRightNeighbour);

    // Для стандартного оператора слева и справа может быть что угодно, кроме знака равно и указателя на блок.
    public static final IRule ruleForDefaultOperatorNeighbour = new ANDRule(
            ruleForNotEqualChar, new NOTRule(ruleForBlockPointer)
    );

    // Пара правил для стандартных операторов
    public static final LeftRightRulesCombo leftRightRulesCombo_ForDefaultOperator =
            new LeftRightRulesCombo(ruleForDefaultOperatorNeighbour, ruleForEqualSignRightNeighbour);

    // Пара правил для if
    public static final LeftRightRulesCombo leftRightRulesCombo_ForIfOperator =
            new LeftRightRulesCombo(ruleForDefaultOperatorNeighbour, ruleForBlockPointer);
    public static final LeftRightRulesCombo leftRightRulesCombo_ForElseOperator =
            new LeftRightRulesCombo(ruleForIfOperator, ruleForBlockPointer);

    // Словарь типа "строковое_представление, правила_для_левого_и_правого_соседа".
    public static final Map<String, LeftRightRulesCombo> stringLeftRightRulesComboMap = new HashMap<>();

    static {
        stringLeftRightRulesComboMap.put("=", leftRightRulesCombo_ForEqualSign);
        stringLeftRightRulesComboMap.put(":=", leftRightRulesCombo_ForEqualSign);
        stringLeftRightRulesComboMap.put("+", leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("-", leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("*", leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("/", leftRightRulesCombo_ForArithmetic);
        stringLeftRightRulesComboMap.put("IF", leftRightRulesCombo_ForIfOperator);
        stringLeftRightRulesComboMap.put("ELSE", leftRightRulesCombo_ForElseOperator);
    }

    public static final Boolean defaultDoesLexemaUsesLeftNeighbour = true;
    // Словарь типа "строковое_представление, использует_ли_данный_оператор_левый_элемент"
    public static final Map<String, Boolean> doesLexemaUsesLeftNeighbour = new HashMap<>();

    static {
        doesLexemaUsesLeftNeighbour.put("NOT", false);
        doesLexemaUsesLeftNeighbour.put("IN", false);
        doesLexemaUsesLeftNeighbour.put("OUT", false);
    }

    // Смещение для поиска левого соседа
    public static final Integer defaultLeftNeighbourOffset = -1;
    public static final Map<String, Integer> leftNeighbourOffset = new HashMap<>();

    static {
        leftNeighbourOffset.put("IF", 1);
    }

    // Смещение для поиска правого соседа
    public static final Integer defaultRightNeighbourOffset = 1;
    public static final Map<String, Integer> rightNeighbourOffset = new HashMap<>();

    static {
        rightNeighbourOffset.put("IF", 2);
    }

    // Может ли оператор быть унарным
    public static final Boolean defaultCanBeUnary = false;
    public static final Map<String, Boolean> canBeUnary = new HashMap<>();

    static {
        canBeUnary.put("-", true);
    }
}
