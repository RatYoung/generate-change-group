package edu.nju.cs.inform.console;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by neo on 16/4/10.
 */
public class DownLoadZip {

    public String downLoadZip(String downLoadURL,String fileName,String downloadLocation){
        int bytesum = 0;
        int byteread = 0;
        //String fileName="4c66abf1a3e77cc0ec099beb58f9874da7fa86d2";
        String downloadURL=downloadLocation+fileName+".zip";
        InputStream inStream=null;
        FileOutputStream fs =null;
        try {
//URL url = new URL(downloadURL + "/" + dateFloder + "/" + "page.zip");
            URL url = new URL(downLoadURL+"/archive/"+fileName+".zip");
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            fs = new FileOutputStream(downloadURL);
            byte[] buffer = new byte[4028];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            System.out.println("Fetch commit "+fileName+" from remote...");
        } catch (Exception e) {
            System.out.println("Download Error: "+e);
            return "false";
        } finally{
            try {
                if(inStream!=null){
                    inStream.close();
                }
            } catch (IOException e) {
                inStream=null;
            }
            try {
                if(fs!=null){
                    fs.close();
                }
            } catch (IOException e) {
                fs=null;
            }
        }
        //fuckyoufuckyoufcufuckyoufuckyoufcufuckyoufuckyoufcufuckyoufuckyoufcufuckyoufuckyoufcufuckyoufuckyoufcufuckyoufuckyoufcu
        return downloadURL;

    }

}
