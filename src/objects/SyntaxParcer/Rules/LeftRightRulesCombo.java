package objects.SyntaxParcer.Rules;

/**
 * Комбинация из левого и правого правила для
 */
public class LeftRightRulesCombo {
    public IRule leftRule;
    public IRule rightRule;

    public LeftRightRulesCombo(IRule leftRule, IRule rightRule) {
        this.leftRule = leftRule;
        this.rightRule = rightRule;
    }
}
