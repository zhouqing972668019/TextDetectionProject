package com.example.dell.textdetectionproject;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DELL on 2018/10/17.
 */

public class FileUtil {

    // 照片路径
    public static String IMG_FILE_PATH
            = Environment.getExternalStorageDirectory() + "/TessTwoPics/";

    //获取某个文件夹下所有文件的名称
    public static List<String> getFileName(String path) {
        List<String> fileNameList=new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (!fs.isDirectory()) {
                fileNameList.add(fs.getName());
            }
        }
        return fileNameList;
    }

    //将指定字符串写入文件
    public static void writeStrToFile(String fileContent, String filePath)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String outputFileName="输出结果："+df.format(new Date());// new Date()为获取当前系统时间
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(filePath + outputFileName);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
