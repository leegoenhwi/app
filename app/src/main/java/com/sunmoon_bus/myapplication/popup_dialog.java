package com.sunmoon_bus.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


    private static final int TYPE_WIFI = 1;
    private static final int TYPE_MOBILE = 2;
    private static final int TYPE_NOT_CONNECTED = 3;

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

        if(getConnectivityStatus(getContext()) != TYPE_NOT_CONNECTED)
        {
            jsk.execute();
        }
        else {
            adapter.addItem("","","메시지가 없습니다.");
        }


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

    //인터넷 연결상태 확인
    private static int getConnectivityStatus(Context context) { //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_MOBILE) {//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE;
            } else if (type == ConnectivityManager.TYPE_WIFI) {//와이파이 연결된것
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
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



    @SuppressLint("StaticFieldLeak")
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {



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

                return null;
            }
            //System.out.println(error);

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {


            if(!time.isEmpty())
            {
                System.out.println("연결확인");
                for(int i = 0;i < time.size();i++)
                {
                    adapter.addItem(time.get(i),title.get(i),content.get(i));

                }
            }

            else
            {
                System.out.println("연결안됨");
                adapter.addItem("","","메시지가 없습니다.");
            }


        }
    }



}
