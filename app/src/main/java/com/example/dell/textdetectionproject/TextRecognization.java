package com.example.dell.textdetectionproject;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2018/3/22.
 */

public class TextRecognization {

    /**
     * TessBaseAPI初始化用到的第一个参数，是个目录。
     */
    public static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    /**
     * 在DATAPATH中新建这个目录，TessBaseAPI初始化要求必须有这个目录。
     */
    public static final String tessdata = DATAPATH  + "tessdata";
    /**
     * TessBaseAPI初始化测第二个参数，就是识别库的名字不要后缀名。
     */
    public static String DEFAULT_LANGUAGE = "eng";

    public static String CHINESE_LANGUAGE = "chi_sim";
    /**
     * assets中的文件名
     */
    public static  String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    /**
     * 保存到SD卡中的完整文件名
     */
    public static  String LANGUAGE_PATH = tessdata + File.separator + DEFAULT_LANGUAGE_NAME;

    private static final String TAG = "TextRecognization";

    //bitmap的文字提取
    public static List<Rect> doOcr(Bitmap bitmap)
    {
        TessBaseAPI baseAPI=new TessBaseAPI();
        baseAPI.init(DATAPATH,DEFAULT_LANGUAGE);
        bitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        baseAPI.setImage(bitmap);
        List<Rect> tempBoxes=baseAPI.getWords().getBoxRects();
//        for(Rect r:tempBoxes)
//        {
//            POI poi=new POI();
//            poi.setRect(r);
//            baseAPI.setRectangle(r);
//            poi.setText(baseAPI.getUTF8Text());
//            poiList.add(poi);
//            System.out.println("rect:"+r.toString()+",text:"+baseAPI.getUTF8Text().toString());
//        }
        bitmap.recycle();

        return tempBoxes;
    }

    //bitmap的文字识别
    public static String getText(Bitmap bitmap,String language)
    {
        List<String> textList=new ArrayList<>();
        TessBaseAPI baseAPI=new TessBaseAPI();
        //baseAPI.init(DATAPATH,DEFAULT_LANGUAGE);
        baseAPI.init(DATAPATH,language);
        bitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        baseAPI.setImage(bitmap);
        String outputText = baseAPI.getUTF8Text();
        baseAPI.end();
        bitmap.recycle();
//        List<Rect> tempBoxes=baseAPI.getWords().getBoxRects();
//        for(Rect r:tempBoxes)
//        {
//            baseAPI.setRectangle(r);
//            String text=baseAPI.getUTF8Text();
//            System.out.println("rect:"+r.toString()+",text:"+text);
//            textList.add(text);
//        }
//        bitmap.recycle();
//        System.out.println("textAll:"+baseAPI.getUTF8Text());
        return outputText;
    }

    //返回框了文字区域的bitmap
    public static Bitmap getAnnotatedBitmap(Bitmap bitmap,List<Rect> wordBoxes,List<String> textList) {
        Bitmap bitmap_temp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap_temp);
        Paint paint = new Paint();
        for (int i=0;i<wordBoxes.size();i++)
        {
            Rect r=wordBoxes.get(i);
            paint.setAlpha(0xFF);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            canvas.drawRect(r,paint);
            System.out.println("rect:"+r.toString());
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(7);
            paint.setAntiAlias(true);
            paint.setTextSize(100);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(textList.get(i),r.left,r.top,paint);
        }
        return bitmap_temp;
    }

    public static Bitmap getBitmapFromPath(String imgPath,ContentResolver contentResolver) throws FileNotFoundException {
        BitmapFactory.Options options=new BitmapFactory.Options();
        //options.inSampleSize=(int)7.5; /*图片长宽方向缩小倍数*/
        //options.inJustDecodeBounds=false;

        File file = new File(imgPath);
        Uri uri = Uri.fromFile(file);
        Bitmap bitmap=BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        return bitmap;
    }

    /**
     * 将assets中的识别库复制到SD卡中
     *
     * @param path 要存放在SD卡中的 完整的文件名。这里是"/storage/emulated/0//tessdata/chi_sim.traineddata"
     * @param name assets中的文件名 这里是 "chi_sim.traineddata"
     */
    public static void assets2SD(Context context, String path, String name) {
        Log.i(TAG, "assets2SD: " + path);
        Log.i(TAG, "assets2SD: " + name);

        //如果存在就删掉
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        if (!f.exists()) {
            File p = new File(f.getParent());
            if (!p.exists()) {
                p.mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getAssets().open(name);
            File file = new File(path);
            os = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
