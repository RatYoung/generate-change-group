package commitFinder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CertainMsg {
	static String get(String filePath) throws IOException {
		File file = new File(filePath);
//		String content =  FileUtils.readFileToString(file, "utf-8");
		List<String> lines = FileUtils.readLines(file, "utf-8");
		String content = "";
		for(String line: lines) {
			if(line.startsWith("    "))
				line = line.substring(4);
			content += line + "\n";
		}
		content = content.trim();
		content = content.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "").replaceAll("\\r", "");
		return content;
	}
}
