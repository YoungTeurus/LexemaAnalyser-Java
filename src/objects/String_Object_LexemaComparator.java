package objects;

import interfaces.IComparator;

/**
 * Сравнение строковой лексемы и лексемы-объекта.
 */
public class String_Object_LexemaComparator implements IComparator {
    /**
     * @param stored_value Объект из HashTable - объект-лексема.
     * @param compared_value Объект, переданный для сравнения (ключ) - строковая лексема.
     */
    @Override
    public boolean compare(Object stored_value, Object compared_value) throws IllegalArgumentException {
        if (compared_value == null)
            return false;
        if (stored_value.getClass() != Lexema.class)
            throw new IllegalArgumentException("stored_value не является объектом класса Lexema.");
        if (compared_value.getClass() != String.class)
            throw new IllegalArgumentException("compared_value не является объектом класса String");
        return ((Lexema)stored_value).get_char().equals((String)compared_value);
    }
}
