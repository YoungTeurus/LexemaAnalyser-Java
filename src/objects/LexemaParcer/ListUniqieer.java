package objects.LexemaParcer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ListUniqieer<T> {
    /**
     * Делает все объекты списка уникальными, убирая повторения.
     * @param list Список объектов.
     * @return Set объектов.
     */
     public List<T> unique_list(List<T> list){
        return new ArrayList<>(new HashSet<T>(list));
    }
}
