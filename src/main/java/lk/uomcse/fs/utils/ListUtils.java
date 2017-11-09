package lk.uomcse.fs.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ListUtils {
    public static <T> List<T> randomSubList(List<T> list, int start, int maxSize) {
        Random random = new Random();
        Collections.shuffle(list);
        int randomIndex = random.nextInt() % maxSize + start;
        return list.subList(0, randomIndex);
    }

    public static <T> List<T> randomSubList(List<T> list, int size) {
        Collections.shuffle(list);
        try {
            return list.subList(0, size);
        } catch (IndexOutOfBoundsException e) {
            return list;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
}
