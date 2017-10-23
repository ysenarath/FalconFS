package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import org.junit.Test;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Iterator;
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

        cacheService.update(new Node(ipAddresses[0], ports[0]), Arrays.asList(fileNames[0]));
        cacheService.update(new Node(ipAddresses[1], ports[1]), Arrays.asList(fileNames[1]));
        cacheService.update(new Node(ipAddresses[2], ports[1]), Arrays.asList(fileNames[3]));

        List<Node> results1 = cacheService.search("Windows");
        List<Node> results2 = cacheService.search("Windows 8");

        List<Node> expected1 = Arrays.asList(
                new Node(ipAddresses[0], ports[0]),
                new Node(ipAddresses[1], ports[1]),
                new Node(ipAddresses[2], ports[1]));

        List<Node> expected2 = Arrays.asList(
                new Node(ipAddresses[0], ports[0]),
                new Node(ipAddresses[1], ports[1]));


        assertArrayEquals(results1.toArray(),expected1.toArray());
        assertArrayEquals(results2.toArray(),expected2.toArray());
    }


}