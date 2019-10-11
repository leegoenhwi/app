package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

public class infor_Dialog extends Dialog {

    private View.OnClickListener mLeftClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.4f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);

        setContentView(R.layout.infor_dialog);

        Button mLeftButton = (Button) findViewById(R.id.infor_butt);


        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        }

    }




    infor_Dialog(Context context, View.OnClickListener singleListener) {
        super(context);
        this.mLeftClickListener = singleListener;

    }
}
