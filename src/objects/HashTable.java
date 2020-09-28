package objects;

import interfaces.IComparator;
import interfaces.IHashFunction;

/**
 * Класс хеш-тиблицы, предназначенной для вставки и получения объектов с лучшей сложностью O(1).
 * Используются универсальные хеш-ключи и открытая адрессация, как метод устранения коллизий.
 * Текущая реализация обладает конечным размером и не предназначена для удаления элементов.
 */
public class HashTable {
    final private int _table_size;

    public Object[] get_table() {
        return _table;
    }

    final private Object[] _table;
    final private IHashFunction _hash_function;

    /**
     * Инициализирует хеш-таблицу, создавая массив размером size.
     * Для всех последующих действий используется функция hash_function.
     * @param size Размер хеш-таблицы.
     * @param hash_function Хеш-функция.
     */
    public HashTable(int size, IHashFunction hash_function){
        _table_size = size;
        _table = new Object[size];
        _hash_function = hash_function;
    }

    /**
     * Вставляет объект в хеш-таблицу.
     * В сложном случае: поиск по хеш-табилце происходит по key, осуществляется вставка value.
     * @param key Ключ для хеширования. Заносится в таблицу, если не указано value.
     * @param value (Необязательно) Объект, заносимый в хеш-таблицу вместо key.
     * @return Возвращает индекс вставленного элемента в случае нахождения свободного места, или null,
     *  если хеш-таблица полна.
     */
    public Integer insert(Object key, Object value){
        // Если value не null:
        Object inserted_value = value == null ? key : value;

        int calculated_hash = _hash_function.hash(key);
        if (_table[calculated_hash] == null){
            // Если ячейка пуста...
            _table[calculated_hash] = inserted_value;
            return calculated_hash;
        }
        else if (!(new SimpleComparator().compare(_table[calculated_hash], inserted_value))){
            // Если ячейка занята чем-то, кроме этого же элемента, начинаем двигаться по таблице далее
            // в поисках свободной ячейки.
            int actual_hash = (calculated_hash + 1) % _table_size;
            while (_table[actual_hash] != null){
                actual_hash = (actual_hash + 1) % _table_size;
                // Если вернулись туда, откуда шли, значит таблица полна.
                if (actual_hash == calculated_hash)
                    return null;
            }
            // Сюда попадаем при нахождении свободной ячейки
            _table[actual_hash] = inserted_value;
            return actual_hash;
        }
        // Сюда попадаем, если данный объект уже добавлен и находится на calculated_hash:
        return calculated_hash;
    }

    /**
     * Возвращает индекс объекта в хеш-таблице, если он был найден по ключу key.
     *         В сложном случае: проверка на совпадение объекта осуществляется с использованием функции compare_func.
     * @param key Ключ для поиска.
     * @param comparator (Необязательно) Функция, с помощью которой проверяется соответствие хранимых объектов в
     *  хеш-таблице с key.
     * @return Индекс объекта в хеш-таблице или null, если он не был найден.
     */
    public Integer get_index(Object key, IComparator comparator){
        // Если comparator не null:
        IComparator _comparator = comparator == null ? new SimpleComparator() : comparator;

        int calculated_hash = _hash_function.hash(key);
        // Если элемента точно нет:
        if (_table[calculated_hash] == null)
            return null;
        // Если на месте элемента он сам:
        if (_comparator.compare(_table[calculated_hash], key))
            return calculated_hash;
        // Если на его месте что-то другое, ищем его дальше по таблице, пока не найдём его самого, или None,
        // или вернёмся в начальный индекс.
        int actual_hash = (calculated_hash + 1) % _table_size;
        while (!(_comparator.compare(_table[actual_hash], key))){
            actual_hash = (actual_hash + 1) % _table_size;
            // Если не нашли элемент, или обошли таблицу покругу:
            if ((_table[actual_hash] == null) || actual_hash == calculated_hash)
                return null;
        }
        // Попадаем сюда, если нашли совпадающий элемент.
        return actual_hash;
    }

    /**
     * Возвращает объект в хеш-таблице, если он был найден по ключу key.
     * В сложном случае: проверка на совпадение объекта осуществляется с использованием функции compare_func.
     * @param key Ключ для поиска.
     * @param comparator  (Необязательно) Функция, с помощью которой проверяется соответствие хранимых объектов в
     *  хеш-таблице с key.
     * @return Найденный объект или null, если он не был найден.
     */
    public Object get_value(Object key, IComparator comparator){
        Integer object_index = get_index(key, comparator);
        return object_index != null ? _table[object_index] : null;
    }
}
