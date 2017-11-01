package lk.uomcse.fs.utils;

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
}
