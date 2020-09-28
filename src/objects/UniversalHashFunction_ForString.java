package objects;

import interfaces.IHashFunction;

import java.util.Random;

/**
 * Универсальная хеш-функция для строк.
 */
public final class UniversalHashFunction_ForString implements IHashFunction {

    private final float _C;
    private final int _hash_table_size;

    public UniversalHashFunction_ForString(int hash_table_size){
        _C = new Random().nextFloat();
        _hash_table_size = hash_table_size;
    }

    @Override
    public int hash(Object obj) throws IllegalArgumentException {
        // Если передали не строку:
        if (!(obj.getClass().equals(String.class))){
            throw new IllegalArgumentException("Функция хеширования предназначена только для типа String," +
                    " было передано "+obj.getClass());
        }

        int _sum = 0;
        int i = 0;

        for (char chr : ((String)obj).toCharArray()){
            _sum += (int)chr * ((i + 1) % 13);
            i =+ 1;
        }

        return (int) Math.floor(_hash_table_size * ((_C * _sum) % 1));
    }
}
