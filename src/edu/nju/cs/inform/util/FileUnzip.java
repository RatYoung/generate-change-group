package edu.nju.cs.inform.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Created by niejia on 16/2/24.
 */
public class FileUnzip {

    public static void extract(String source, String target) {
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(target);
            System.out.println("Unzip file " + zipFile.getFile().getName() + " successfully");
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String file = "/Users/Che/Documents/Projects/c6f082b6d22f102759bb9c7c72225848afe15d58 and 4c66abf1a3e77cc0ec099beb58f9874da7fa86d2/c6f082b6d22f102759bb9c7c72225848afe15d58.zip";
        String outputPath = "/Users/Che/Documents/Projects/c6f082b6d22f102759bb9c7c72225848afe15d58 and 4c66abf1a3e77cc0ec099beb58f9874da7fa86d2/";

        FileUnzip.extract(file, outputPath);
    }
}
