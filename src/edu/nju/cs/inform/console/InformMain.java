package edu.nju.cs.inform.console;

import edu.nju.cs.inform.core.diff.CodeElementsComparer;
import edu.nju.cs.inform.core.ir.IRModelConst;
import edu.nju.cs.inform.core.ir.Retrieval;
import edu.nju.cs.inform.core.type.ArtifactsCollection;
import edu.nju.cs.inform.core.type.CodeElementChange;
import edu.nju.cs.inform.io.ArtifactsReader;
import edu.nju.cs.inform.util.JavaElement;

import java.io.File;
import java.util.*;

/**
 * Created by neo on 16/4/8.
 */
public class InformMain {
    public static void main(String[] args){
        String downLoadUrl = args[0];
        String new_Version_file = args[1];
        String old_Version_file = args[2];
        String requirement_file = args[3];
        String old_source_Path;
        String new_source_Path;
        String requirement_Path;

        String projectPath = getProjectPath();

       // String filePath = createFile(downLoadUrl, new_Version_file, old_Version_file, projectPath);
        //create file
        String filePath = projectPath+"test/";
        File fp = new File(filePath);
        fp.mkdir();
        //download
        DownLoadZip oldVersion_Zip = new DownLoadZip();
        oldVersion_Zip.downLoadZip(downLoadUrl, old_Version_file, filePath);
        DownLoadZip newVersion_Zip = new DownLoadZip();
        newVersion_Zip.downLoadZip(downLoadUrl, new_Version_file, filePath);
        //decompressing
        ZipDecompressing oldVersion_directory = new ZipDecompressing();
        ArrayList<String> oldcode_list = oldVersion_directory.Ectract(filePath + old_Version_file + ".zip", filePath);

        old_source_Path=getOldsourcePath(oldcode_list);

        ZipDecompressing newVersion_directory = new ZipDecompressing();
        ArrayList<String> newcode_list = newVersion_directory.Ectract(filePath + new_Version_file + ".zip", filePath);

        new_source_Path=getNewsourcePath(newcode_list);

        requirement_Path=getUnzipfilePath(newcode_list,filePath)+requirement_file;

        listComparer(new_source_Path, old_source_Path, requirement_Path, filePath);

    }
    private  static String getUnzipfilePath(ArrayList<String> newcode_list,String filePath){

        File f=new File(newcode_list.get(0));

        String unzipfileName=InformMain.getUnzipfileName(f.getAbsolutePath());

        return filePath+unzipfileName+"/";
    }

    private static String getNewsourcePath(ArrayList<String> newcode_list) {
        String new_source_Path=null;
        for(int i = 0; i<newcode_list.size(); i++){
            String file_name=newcode_list.get(i);
            String file=JavaElement.getIdentifier(InformMain.getfileName(file_name));
            if(file.equals("java")){
                File newfile=new File(file_name);
                new_source_Path=newfile.getParent();
            }
           /* if(JavaElement.getIdentifier(file_name)==".txt"){
                File requirementFile=new File(file_name);
                requirement_Path=requirementFile.getParent();
            }*/
        }
        return new_source_Path;
    }

    private static String getOldsourcePath(ArrayList<String> oldcode_list) {
        String old_source_Path = null;
        for(int i = 0; i<oldcode_list.size(); i++){
            String file_name=oldcode_list.get(i);
            String file=JavaElement.getIdentifier(InformMain.getfileName(file_name));
            if(file.equals("java")){
                File oldfile=new File(file_name);
                old_source_Path=new String(oldfile.getParent());
            }
        }
        return old_source_Path;
    }

    private static void listComparer(String new_source_path, String old_source_path, String requirement_Path, String filePath) {
        CodeElementsComparer comparer;
        System.out.println("-----------------Code Elements Diff-----------------");
        comparer = new CodeElementsComparer(new_source_path, old_source_path);
        comparer.diff();
        Set<CodeElementChange> codeElementChangeList = comparer.getCodeElementChangesList();
        for (CodeElementChange elementChange : codeElementChangeList) {
            System.out.println(elementChange.getElementName() + " " + elementChange.getElementType() + " " + elementChange.getChangeType());
        }

        System.out.println("-----------------Top10 Requirement Elements-----------------");

        // get change description from code changes
        ArtifactsCollection changeDescriptionCollection = comparer.getChangeDescriptionCollection();
        final ArtifactsCollection requirementCollection = ArtifactsReader.getCollections(requirement_Path, ".txt");

        // retrieval change description to requirement
        Retrieval retrieval = new Retrieval(changeDescriptionCollection, requirementCollection, IRModelConst.VSM);
        retrieval.tracing();

        Map<String, Double> candidatedOutdatedRequirementsRank = retrieval.getCandidateOutdatedRequirementsRank();
        //将map转换成list
        java.util.List<Map.Entry<String, Double>> list = new ArrayList<>(candidatedOutdatedRequirementsRank.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            //降序排列
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        int index=0;
        for (Map.Entry<String, Double> map : list) {
            if(index<10) {
                System.out.println(map.getKey() + "  " + String.valueOf(map.getValue()));
                index++;
            }
            else{
                break;
            }
        }
    }
/*
    private static String createFile(String downLoadUrl, String new_Version_file, String old_Version_file, String projectPath) {
        //create file
        String filePath = projectPath+"test/";
        File fp = new File(filePath);
        fp.mkdir();
        //download
        DownLoadZip oldVersion_Zip = new DownLoadZip();
        oldVersion_Zip.downLoadZip(downLoadUrl, old_Version_file, filePath);
        DownLoadZip newVersion_Zip = new DownLoadZip();
        newVersion_Zip.downLoadZip(downLoadUrl, new_Version_file, filePath);
        //decompressing
        ZipDecompressing oldVersion_directory = new ZipDecompressing();
        ZipDecompressing newVersion_directory = new ZipDecompressing();
        ArrayList<String> oldcode_list = oldVersion_directory.Ectract(filePath + old_Version_file + ".zip", filePath);
        for(int i=0;i<oldcode_list.size();i++){
            String file_name=oldcode_list.get(i);
            if(JavaElement.getIdentifier(file_name)==".java"){
                File oldfile=new File(file_name);
                String old_source_Path=oldfile.getParent();
            }
        }
        ArrayList<String> newcode_list = newVersion_directory.Ectract(filePath + new_Version_file + ".zip", filePath);
        for(int i=0;i<oldcode_list.size();i++){
            String file_name=newcode_list.get(i);
            if(JavaElement.getIdentifier(file_name)==".java"){
                File newfile=new File(file_name);
                String new_source_Path=newfile.getParent();
            }
            if(JavaElement.getIdentifier(file_name)==".txt"){
                File requirementFile=new File(file_name);
                String requirementPath=requirementFile.getParent();
            }
        }
        return filePath;
    }
*/
    private static String getProjectPath() {

        String desktop=System.getProperty("user.home");
        String projectPath = desktop+"/Documents/Projects/";
        File project = new File(projectPath);
        // 目录已存在创建文件夹
        if (!project.exists()) {
            project.mkdir();
        }
        return projectPath;
    }

    public static String getfileName(String name) {
        String[] tokens = name.split("\\/");
        return tokens[tokens.length - 1];
    }

    private static String getUnzipfileName(String name){
        String[] tokens=name.split("\\/");
        return tokens[6];
    }

}
