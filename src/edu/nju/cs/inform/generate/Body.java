package edu.nju.cs.inform.generate;

import edu.nju.cs.inform.generate.NewRetro;

public class Body {
	public static void main(String[] args) {
		NewRetro re = new NewRetro();

		String newVerPath = "C:\\Users\\24541\\Desktop\\������Ŀ\\source_codes\\�����ÿ�Դ����\\commons-io-master-java";
		String oldVerPath = "C:\\Users\\24541\\Desktop\\������Ŀ\\source_codes\\�����ÿ�Դ����\\commons-io-head~50-java";
		String reqPath = "C:\\Users\\24541\\Desktop\\req-swing-demo\\data\\sample\\AquaLush_Requirement";
		
		re.process(newVerPath, oldVerPath, reqPath);
	}
}
