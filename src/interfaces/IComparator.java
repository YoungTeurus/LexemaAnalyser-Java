package interfaces;

/**
 * Класс, использующийся в HashTable. Реализует одну функцию - compare.
 */
public interface IComparator {
    /**
     * Возвращает результат сравнения двух объектов.
     * @param stored_value Объект из HashTable.
     * @param compared_value Объект, переданный для сравнения (ключ).
     * @return boolean-значение результата сравнения. Возвращает False, если compared_value = null.
     * @throws IllegalArgumentException Вызывается, если объекты имеют неправильный тип.
     */
    boolean compare(Object stored_value, Object compared_value) throws IllegalArgumentException;
}
