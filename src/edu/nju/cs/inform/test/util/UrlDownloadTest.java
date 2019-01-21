package edu.nju.cs.inform.test.util;

import edu.nju.cs.inform.util.UrlDownload;
import org.junit.Test;

public class UrlDownloadTest {

    @Test
    public void testFileDownload() throws Exception {
        String fAddress = "https://github.com/qynnine/INFORM/archive/15b69e3c52d4b5b973ce5334540b6dc832cfb98c.zip";
        String destinationDir = "data/demo/";

        UrlDownload.fileDownload(fAddress, destinationDir);
    }
}