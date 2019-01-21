package edu.nju.cs.inform.generate;

import edu.nju.cs.inform.generate.NewRetro;

public class Body {
	public static void main(String[] args) {
		NewRetro re = new NewRetro();

		String newVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-master-java";
		String oldVerPath = "C:\\Users\\24541\\Desktop\\创新项目\\source_codes\\测试用开源工程\\commons-io-head~50-java";
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		re.process(newVerPath, oldVerPath, reqPath);
	}
}
