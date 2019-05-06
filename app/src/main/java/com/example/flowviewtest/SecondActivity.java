package com.example.flowviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    Button btn_go;
    EditText et_input;
    Button btn_back;
    X5WebView x5_web_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btn_go=findViewById(R.id.btn_go);
        et_input=findViewById(R.id.et_input);
        x5_web_view=findViewById(R.id.x5_web_view);
        btn_back=findViewById(R.id.btn_back);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_input.getText().toString())){
                    Toast.makeText(SecondActivity.this,"输入为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                x5_web_view.loadUrl(et_input.getText().toString());
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x5_web_view.goBack();
            }
        });
        x5_web_view.loadUrl("http://www.mtotoo.com/play/57827-1-1.html?57827-1-3");
    }
}
