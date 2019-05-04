package generate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReadRetro {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File("E:\\Desktop\\workspace\\创新项目：紧密度追踪过时需求\\mavenHistory\\Retros\\5cbc294e72c79955fb93c6434e9ac8ae7d9206cc.retro");
	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	    NewRetro re = (NewRetro) ois.readObject();
	    System.out.println(re.codeElementChangeList);
	    ois.close();
	}
}
