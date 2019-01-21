package edu.nju.cs.inform.test.util;

import edu.nju.cs.inform.util.FileUnzip;

public class FileUnzipTest {

    @org.junit.Test
    public void testExtract() throws Exception {
        String file = "data/demo/15b69e3c52d4b5b973ce5334540b6dc832cfb98c.zip";
        String outputPath = "data/demo/";
        FileUnzip.extract(file, outputPath);
    }
}