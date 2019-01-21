package edu.nju.cs.inform.core.preprocess;


import edu.nju.cs.inform.core.preprocess.snowball.EnglishStemmer;

/**
 * Created by niejia on 15/2/23.
 */
public class Snowball {

    public static String stemming(String input) {
        StringBuilder sb = new StringBuilder();

        String words[] = input.split(" ");

        EnglishStemmer stemmer = new EnglishStemmer();

        for (String word : words) {
            stemmer.setCurrent(word);
            stemmer.stem();
            sb.append(stemmer.getCurrent());
            sb.append(" ");
        }

        return sb.toString();
    }
}
