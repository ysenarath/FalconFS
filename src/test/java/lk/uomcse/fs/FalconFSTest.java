package lk.uomcse.fs;

import lk.uomcse.fs.entity.BootstrapServer;
import org.junit.Test;

import static org.junit.Assert.*;

public class FalconFSTest {
    private static final String BS_IP = "localhost";
    private static final int BS_PORT = 55555;
    // CSE 1
    private static final String FS_NAME = "cse1";
    private static final String FS_IP = "localhost";
    private static final int FS_PORT = 5555;
    // CSE 2
    private static final String FS_NAME_1 = "cse2";
    private static final String FS_IP_1 = "localhost";
    private static final int FS_PORT_1 = 5556;

    @Test
    public void bootstrap() throws Exception {
        BootstrapServer bc = new BootstrapServer(BS_IP, BS_PORT);
        FalconFS fs = new FalconFS(FS_NAME, FS_IP, FS_PORT, bc);
        boolean success = fs.start();
        assertTrue(success);
    }

    @Test
    public void join() throws Exception {
        BootstrapServer bc = new BootstrapServer(BS_IP, BS_PORT);
        FalconFS fs = new FalconFS(FS_NAME, FS_IP, FS_PORT, bc);
        FalconFS fs1 = new FalconFS(FS_NAME_1, FS_IP_1, FS_PORT_1, bc);
        boolean s1 = fs.start();
        boolean s2 = fs1.start();
        fs.getFilenames().add("sunshine");
        fs.getFilenames().add("windows 10");
        fs1.query("windows");
    }
}
