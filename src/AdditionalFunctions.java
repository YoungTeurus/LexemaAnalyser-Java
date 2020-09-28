import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AdditionalFunctions {
    /**
     * Делает все объекты списка уникальными, убирая повторения.
     * @param list Список объектов.
     * @return Set объектов.
     */
     public static Set<Object> unique_list(List<Object> list){
        return new HashSet<Object>(list);
    }
}
