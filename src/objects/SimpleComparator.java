package objects;

import interfaces.IComparator;

public class SimpleComparator implements IComparator {
    @Override
    public boolean compare(Object stored_value, Object compared_value) throws IllegalArgumentException {
        if (compared_value == null)
            return false;
        if (stored_value.getClass() != compared_value.getClass())
            throw new IllegalArgumentException("Типы аргументов не совпадают!");
        return stored_value.equals(compared_value);
    }
}
