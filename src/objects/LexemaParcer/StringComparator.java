package objects.LexemaParcer;

import interfaces.IComparator;

/**
 * Сравнение двух строк.
 */
public class StringComparator implements IComparator {
    @Override
    public boolean compare(Object stored_value, Object compared_value) throws IllegalArgumentException {
        if (compared_value == null)
            return false;
        if ((stored_value.getClass() != String.class)
                || compared_value.getClass() != String.class)
            throw new IllegalArgumentException("Переданы не String-аргументы!");
        return stored_value.equals(compared_value);
    }
}
