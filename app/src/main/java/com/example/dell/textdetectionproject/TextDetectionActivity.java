package com.example.dell.textdetectionproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TextDetectionActivity extends AppCompatActivity {

    private Button btn_eng;
    private Button btn_chinese;
    private Button btn_eng_binary;
    private Button btn_chinese_binary;
    private TextView tv_picNum;
    private TextView tv_result;
    private TextView tv_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);
        initViews();
    }

    public void initViews(){
        btn_eng = (Button) findViewById(R.id.btn_eng);
        btn_chinese = (Button) findViewById(R.id.btn_chinese);
        btn_eng_binary = (Button) findViewById(R.id.btn_eng_binary);
        btn_chinese_binary = (Button) findViewById(R.id.btn_chinese_binary);
        tv_picNum = (TextView) findViewById(R.id.tv_picNum);
        tv_result = (TextView) findViewById(R.id.tv_result);
        tv_time = (TextView) findViewById(R.id.tv_time);
        btn_eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
