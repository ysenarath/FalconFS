package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Queue;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CacheServiceTest {

    CacheService cacheService = new CacheService(5, 3);

    String[][] fileNames = new String[][]{
            {"Windows", "Call of duty", "Windows 8", "RedHat"},
            {"Avengers", "Windows 8", "Arrow"},
            {"Visual studio", "Arrow", "Call of duty"},
            {"Windows", "Linux", "Linux"}};

    String[] ipAddresses = new String[]{
            "192.168.12.1",
            "192.168.42.1",
            "192.168.54.59"};

    int[] ports = new int[]{2122, 1222, 5342};

    @Test
    public void update() throws Exception {
        cacheService.update(new Node(ipAddresses[0], ports[0]), Arrays.asList(fileNames[0]));

        Queue<Node> nodes = cacheService.getCacheTable().get("windows");

        List<String> keyWords = new ArrayList<>();
        for (String fileName : fileNames[0]) {
            for (String keyWord : fileName.toLowerCase().trim().split(" +")) {
                if (keyWords.contains(keyWord)) {
                    keyWords.remove(keyWord);
                }
                keyWords.add(keyWord);
            }
        }

        int inCache = cacheService.getIndexSize();
        for (int i = (keyWords.size() - 1); i >= 0; i--) {
            if (inCache > 0){
                assertTrue(cacheService.getCacheTable().containsKey(keyWords.get(i)));
                inCache--;
            }else {
                assertFalse(cacheService.getCacheTable().containsKey(keyWords.get(i)));
            }

        }
    }

    @Test
    public void search() throws Exception {

    }


}