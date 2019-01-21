package edu.nju.cs.inform.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by niejia on 16/5/16.
 */
public class JGitFunction {


    public static void write(String filePath, Repository repository, String suffix, String format) throws IOException, GitAPIException {
        // this is the file that we write the archive to
        File file = File.createTempFile(filePath, suffix);
        try (OutputStream out = new FileOutputStream(file)) {
            // finally call the ArchiveCommand to write out using the various supported formats
            try (Git git = new Git(repository)) {
                git.archive()
                        .setTree(repository.resolve("master"))
                        .setFormat(format)
                        .setOutputStream(out)
                        .call();
            }
        }

        System.out.println("Wrote " + file.length() + " bytes to " + file);
    }

}
