package edu.nju.cs.inform.core.preprocess;

import edu.nju.cs.inform.util.AppConfigure;

/**
 * Created by niejia on 15/2/22.
 */
public class ArtifactPreprocessor {

    public static String handlePureTextFile(String str) {
        str = CleanUp.chararctorClean(str);
        str = CleanUp.lengthFilter(str, 3);
        str = CleanUp.tolowerCase(str);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, AppConfigure.Stopwords);
        return str;
    }

    public static String handleJavaFile(String str) {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        str = SentenceSplitter.process(str);

//        str = CleanUp.lengthFilter(str, 3);

        str = CleanUp.tolowerCase(str);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, AppConfigure.Stopwords);
        return str;
    }
}