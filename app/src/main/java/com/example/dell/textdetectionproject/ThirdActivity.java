package com.example.dell.textdetectionproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    // 照片路径
    public static String IMG_FILE_PATH
            = Environment.getExternalStorageDirectory() + "/TextExtractAndRecognition/";

    private static final int PERMISSION_REQUEST_CODE = 0;

    private static final String TAG = "MainActivity";

    private Button btn_run;
    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        btn_run= (Button) findViewById(R.id.btn_run);
        iv_image= (ImageView) findViewById(R.id.iv_image);

        Resources res=getResources();
        final Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.test2);
        iv_image.setImageBitmap(bmp);

        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Rect> tempBoxes =TextRecognization.doOcr(bmp);
                List<String> textList=TextRecognization.getText(bmp,TextRecognization.DEFAULT_LANGUAGE);
                Bitmap bitmap_new = TextRecognization.getAnnotatedBitmap(bmp, tempBoxes,textList);
                iv_image.setImageBitmap(bitmap_new);
            }
        });
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
