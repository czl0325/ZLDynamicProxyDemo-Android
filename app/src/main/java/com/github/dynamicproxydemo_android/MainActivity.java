package com.github.dynamicproxydemo_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dynamicproxydemo_android.MyInjectUtil.ContentView;
import com.github.dynamicproxydemo_android.MyInjectUtil.InjectUtil;
import com.github.dynamicproxydemo_android.MyInjectUtil.OnClick;
import com.github.dynamicproxydemo_android.MyInjectUtil.OnLongClick;
import com.github.dynamicproxydemo_android.MyInjectUtil.BindView;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.text)
    TextView textView;

    @OnClick(R.id.btn1)
    public void myTouch(View view) {
        Toast.makeText(this, "触发了单击事件", Toast.LENGTH_SHORT).show();
    }

    @OnLongClick(R.id.btn1)
    public boolean myLongTouch(View view) {
        Toast.makeText(this, "触发了长按事件", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectUtil.inject(this);

        textView.setText("绑定控件成功");
    }
}
