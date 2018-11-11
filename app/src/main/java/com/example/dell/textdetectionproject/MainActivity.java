package com.example.dell.textdetectionproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.File;

import static com.example.dell.textdetectionproject.SDUtils.assets2SD;

public class MainActivity extends AppCompatActivity {

    private Button btn_textDetection;
    private TextView result;
    private static final String TAG = "MainActivity";

    /**
     * TessBaseAPI初始化用到的第一个参数，是个目录。
     */
    private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    /**
     * 在DATAPATH中新建这个目录，TessBaseAPI初始化要求必须有这个目录。
     */
    private static final String tessdata = DATAPATH  + "tessdata";
    /**
     * TessBaseAPI初始化测第二个参数，就是识别库的名字不要后缀名。
     */
    private static String DEFAULT_LANGUAGE = "eng";
    /**
     * assets中的文件名
     */
    private static  String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    /**
     * 保存到SD卡中的完整文件名
     */
    private static  String LANGUAGE_PATH = tessdata + File.separator + DEFAULT_LANGUAGE_NAME;

    /**
     * 权限请求值
     */
    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final int PICK_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
        btn_textDetection= (Button) findViewById(R.id.btn_textDetection);
        result= (TextView) findViewById(R.id.tv_result);
        btn_textDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources res=getResources();
                Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.test2018);
                String answer=textDetection(bmp);
                result.setText(answer);
            }
        });
    }

    public String textDetection(final Bitmap bitmap)
    {
        String text="";
        if (!checkTraineddataExists()){
            text+=LANGUAGE_PATH+"不存在，开始复制\r\n";
            Log.i(TAG, "run: "+LANGUAGE_PATH+"不存在，开始复制\r\n");
            assets2SD(getApplicationContext(), LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
        }
        text+=LANGUAGE_PATH+"已经存在，开始识别\r\n";
        Log.i(TAG, "run: "+LANGUAGE_PATH+"已经存在，开始识别\r\n");
        long startTime = System.currentTimeMillis();
        Log.i(TAG, "run: kaishi " + startTime);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);
        tessBaseAPI.setImage(bitmap);
        text =text+"识别结果："+ tessBaseAPI.getUTF8Text();
        long finishTime = System.currentTimeMillis();
        Log.i(TAG, "run: jieshu " + finishTime);
        Log.i(TAG, "run: text " + text);
        text = text + "\r\n" + " 耗时" + (finishTime - startTime) + "毫秒";
        final String finalText = text;
        tessBaseAPI.end();
        return finalText;
    }

    public boolean checkTraineddataExists(){
        File file = new File(LANGUAGE_PATH);
        return file.exists();
    }

    /**
     * 请求到权限后在这里复制识别库
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: " + grantResults[0]);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: copy");
                    assets2SD(getApplicationContext(), LANGUAGE_PATH, DEFAULT_LANGUAGE_NAME);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //load OpenCV engine and init OpenCV library
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, getApplicationContext(), mLoaderCallback);
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
}
