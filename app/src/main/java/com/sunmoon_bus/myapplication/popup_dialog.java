package com.sunmoon_bus.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.widget.NestedScrollView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class popup_dialog extends Dialog {
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickLinstener;
    private ListView listView;
    private NestedScrollView popup_scroll;
    private  ListViewAdapter adapter;
    private String notice_url = "http://119.67.32.123:8840/bus_notice.php";
    private ArrayList<String> time = new ArrayList<String>();
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> content = new ArrayList<String>();
    private JsoupAsyncTask jsk;

    @SuppressLint("ClickableViewAccessibility")
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

        listView =(ListView)findViewById(R.id.popup_listview);

        // Adapter 생성
        adapter = new ListViewAdapter();

        listView.setAdapter(adapter);

        //어싱크테스크 객체 생성
        jsk = new JsoupAsyncTask();
        jsk.execute();


        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null) {
            mLeftButton2.setOnClickListener(mLeftClickListener);
        }
        if (mRightClickLinstener != null) {
            mLeftButton.setOnClickListener(mRightClickLinstener);
        }

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

    @Override
    public void onStart()
    {
        System.out.println("온스타트");
        super.onStart();
    }




    popup_dialog(Context context, View.OnClickListener singleListener, View.OnClickListener Listenenr) {
        super(context);
        this.mLeftClickListener = singleListener;
        this.mRightClickLinstener = Listenenr;

    }



    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        int error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            System.out.println("팝업 어싱크 테스크 시작");

            try {
                Document doc = Jsoup.connect(notice_url).get();

                Elements sets = doc.select("div");

                int i=0;

                for (Element e: sets) {
                    Elements set = doc.select("div."+i+"");
                    String n[] = (set.text().split("&& "));
                    time.add(n[0]);
                    content.add(n[1]);
                    title.add(n[2]);
                    i++;
                }
            } catch (IOException e) {
                System.out.println("트라이 캐치");
                error = 1;
                return null;
            }
            System.out.println(error);
            error = 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {




            if(error == 1)
            {
                adapter.addItem("메시지가 없습니다.","","");
            }
            else
            {
                for(int i = 0;i < time.size();i++)
                {
                    adapter.addItem(time.get(i),title.get(i),content.get(i));

                    }
                }
            }
        }


}
