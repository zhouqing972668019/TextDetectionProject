package com.example.dell.textdetectionproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.util.List;

public class TextDetectionActivity extends AppCompatActivity {

    private Button btn_eng;
    private Button btn_chinese;
    private Button btn_eng_binary;
    private Button btn_chinese_binary;
    private TextView tv_picNum;
    private TextView tv_recognizedNum;
    private TextView tv_result;
    private TextView tv_time;

    private static final String TAG = "TextDetectionActivity";

    private static final int PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);
        initViews();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void initViews(){
        btn_eng = (Button) findViewById(R.id.btn_eng);
        btn_chinese = (Button) findViewById(R.id.btn_chinese);
        btn_eng_binary = (Button) findViewById(R.id.btn_eng_binary);
        btn_chinese_binary = (Button) findViewById(R.id.btn_chinese_binary);
        tv_picNum = (TextView) findViewById(R.id.tv_picNum);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_recognizedNum = (TextView) findViewById(R.id.tv_recognizeNum);
        btn_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eng.setTextColor(0xFFFFFFFF);
                btn_eng.setEnabled(false);
                final List<String> fileNameList = FileUtil.getFileName(FileUtil.IMG_FILE_PATH);
                tv_picNum.setText(fileNameList.size()+"");
                final StringBuilder fileContent = new StringBuilder();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<fileNameList.size();i++){
                            String fileName = fileNameList.get(i);
                            Bitmap bitmap = null;
                            try {
                                String textLine = fileName+",";
                                long startTime = System.currentTimeMillis();
                                bitmap = TextRecognization.getBitmapFromPath(FileUtil.IMG_FILE_PATH + fileName,getContentResolver());
                                String outputText = TextRecognization.getText(bitmap,TextRecognization.DEFAULT_LANGUAGE);
                                System.out.println("recognizationSize:"+(i+1));
                                System.out.println("outputText:"+outputText);
                                textLine += outputText.replace("\n"," ") + ",";
                                long endTime = System.currentTimeMillis();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tv_result.setText(finalTextLine);
//                                        tv_time.setText(((endTime- startTime)/1000) + "");
//                                        tv_result.setText(reconizationNum+"");
//                                    }
//                                });
                                textLine += (endTime - startTime)+"\n";
                                fileContent.append(textLine);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("complete.");
                        FileUtil.writeStrToFile(fileContent.toString(),FileUtil.IMG_FILE_PATH);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_eng.setTextColor(Color.BLACK);
                                btn_eng.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        });
        btn_eng_binary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eng_binary.setTextColor(0xFFFFFFFF);
                btn_eng_binary.setEnabled(false);
                final List<String> fileNameList = FileUtil.getFileName(FileUtil.IMG_FILE_PATH);
                tv_picNum.setText(fileNameList.size()+"");
                final StringBuilder fileContent = new StringBuilder();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<fileNameList.size();i++){
                            String fileName = fileNameList.get(i);
                            Bitmap bitmap = null;
                            String textLine = fileName+",";
                            long startTime = System.currentTimeMillis();
                            Mat binaryImg = binarization(FileUtil.IMG_FILE_PATH + fileName,FileUtil.IMG_FILE_PATH );
                            bitmap = Bitmap.createBitmap(binaryImg.cols(), binaryImg.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(binaryImg, bitmap);
                            //bitmap = TextRecognization.getBitmapFromPath(FileUtil.IMG_FILE_PATH + fileName,getContentResolver());
                            String outputText = TextRecognization.getText(bitmap,TextRecognization.DEFAULT_LANGUAGE);

                            System.out.println("recognizationSize:"+(i+1));
                            System.out.println("outputText:"+outputText);
                            textLine += outputText.replace("\n"," ") + ",";
                            long endTime = System.currentTimeMillis();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        tv_result.setText(finalTextLine);
//                                        tv_time.setText(((endTime- startTime)/1000) + "");
//                                        tv_result.setText(reconizationNum+"");
//                                    }
//                                });
                            textLine += (endTime - startTime)+"\n";
                            fileContent.append(textLine);
                        }
                        System.out.println("complete.");
                        FileUtil.writeStrToFile(fileContent.toString(),FileUtil.IMG_FILE_PATH);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_eng_binary.setTextColor(Color.BLACK);
                                btn_eng_binary.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        });
    }


    //对图片进行二值化
    public static Mat binarization(String oriImg, String outputPath) {
        Mat img = Imgcodecs.imread(oriImg);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        //Imgcodecs.imwrite(outputPath + "outputImg.jpg", img);
        //Core.bitwise_not(img, img);
        //Imgcodecs.imwrite(outputPath + "outputImg2.jpg", img);
        return img;

    }

    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, getApplicationContext(), mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(getApplicationContext(), "需要读写文件权限！", Toast.LENGTH_LONG).show();
        }
    }
    
}
