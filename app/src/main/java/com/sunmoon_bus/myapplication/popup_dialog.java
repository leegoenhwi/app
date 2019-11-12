package com.sunmoon_bus.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.widget.NestedScrollView;

import java.util.Objects;

public class popup_dialog extends Dialog {
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickLinstener;
    private ListView listView;
    private NestedScrollView popup_scroll;
    private  ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.4f;
        Objects.requireNonNull(getWindow()).setAttributes(lpWindow);

        //레이아웃 셋
        setContentView(R.layout.popup_layout);

        popup_scroll = (NestedScrollView) findViewById(R.id.popup_scroll);

        Button mLeftButton2 = (Button) findViewById(R.id.popup_btn2);

        Button mLeftButton = (Button) findViewById(R.id.popup_btn);


        //내용 초기화
        listview_insert();


        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null) {
            mLeftButton2.setOnClickListener(mLeftClickListener);
        }
        if (mRightClickLinstener != null) {
            mLeftButton.setOnClickListener(mRightClickLinstener);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void listview_insert()
    {
        listView =(ListView)findViewById(R.id.popup_listview);

        // Adapter 생성
        adapter = new ListViewAdapter();

        listView.setAdapter(adapter);

        adapter.addItem("2019.11.02","11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111","타이틀");
        adapter.addItem("2019.10.21","내용","타이틀");
        adapter.addItem("2019.10.07","내용","타이틀");
        adapter.addItem("2019.09.29","내용","타이틀");
        adapter.addItem("2019.08.15","내용","타이틀");
        adapter.addItem("2019.08.12","내용","타이틀");
        adapter.addItem("2019.08.09","내용","타이틀");
        adapter.addItem("2019.08.07","내용","타이틀");
        adapter.addItem("2019.08.05","내용","타이틀");

        //터치 이벤트
        listView.setOnTouchListener(new View.OnTouchListener() {        //리스트뷰 터취 리스너
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup_scroll.requestDisallowInterceptTouchEvent(true);    // 리스트뷰에서 터취가되면 스크롤뷰만 움직이게
                return false;
            }
        });

        //클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View decs_view = view.findViewById(R.id.list_desc);

                if(decs_view.getVisibility() == View.GONE)
                {
                    decs_view.setVisibility(View.VISIBLE);
                }
                else{
                    decs_view.setVisibility(View.GONE);
                }
            }
        });

    }


    popup_dialog(Context context, View.OnClickListener singleListener, View.OnClickListener Listenenr) {
        super(context);
        this.mLeftClickListener = singleListener;
        this.mRightClickLinstener = Listenenr;

    }



}
