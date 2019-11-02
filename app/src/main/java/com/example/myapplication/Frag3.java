package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Frag3 extends Fragment {
    private View view;

    private SwipeRefreshLayout refresh_Layout3;
    private SingleSelectToggleGroup singleSelectToggleGroup;
    private SingleSelectToggleGroup singleSelectToggleGroup2;


    private NestedScrollView scrollView_start = null;
    private NestedScrollView scrollView_arrive = null;

    private LinearLayout scroll_vertical_layout1 = null;
    private LinearLayout scroll_vertical_layout2 = null;

    private TextView[] textViews1;
    private TextView[] textViews2;

    int curr_Day;

    private String weekDay = "";

    private ArrayList<String> day_cheonan = new ArrayList<String>();
    private ArrayList<String> day_asan = new ArrayList<String>();
    private ArrayList<String> day_terminal = new ArrayList<String>();
    private ArrayList<String> fri_cheonan = new ArrayList<String>();
    private ArrayList<String> fri_asan = new ArrayList<String>();
    private ArrayList<String> fri_terminal = new ArrayList<String>();
    private ArrayList<String> sat_cheonan_asan = new ArrayList<String>();
    private ArrayList<String> sat_terminal = new ArrayList<String>();
    private ArrayList<String> sun_cheonan_asan = new ArrayList<String>();
    private ArrayList<String> sun_terminal = new ArrayList<String>();
    private ArrayList<String> day_cheonan_rev = new ArrayList<String>();
    private ArrayList<String> day_asan_rev = new ArrayList<String>();
    private ArrayList<String> day_terminal_rev = new ArrayList<String>();
    private ArrayList<String> fri_cheonan_rev = new ArrayList<String>();
    private ArrayList<String> fri_asan_rev = new ArrayList<String>();
    private ArrayList<String> fri_terminal_rev = new ArrayList<String>();
    private ArrayList<String> sat_cheonan_rev = new ArrayList<String>();
    private ArrayList<String> sat_asan_rev = new ArrayList<String>();
    private ArrayList<String> sat_terminal_rev = new ArrayList<String>();
    private ArrayList<String> sun_cheonan_rev = new ArrayList<String>();
    private ArrayList<String> sun_asan_rev = new ArrayList<String>();
    private ArrayList<String> sun_terminal_rev = new ArrayList<String>();
    //쓰는 Arraylist들

    private String url_day_cheonan = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_01.aspx";
    private String url_day_asan = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_02.aspx";
    private String url_day_terminal = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_02.aspx";
    private String url_sat_cheonan_asan = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_02_01.aspx";
    private String url_sat_terminal = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_02_02.aspx";
    private String url_sun_cheonan_asan = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_03_01.aspx";
    private String url_sun_terminal = "https://lily.sunmoon.ac.kr/Page/About/About08_04_02_03_02.aspx";
    //크롤링에 필요한 url들

    private DBHelper dbhelper;
    //dbhelper클래스에 있는 메소드를 이용하여 db에 여러가지 접근이 가능하다

    private ProgressDialog asyncDialog;

    private JAT task;

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        dbhelper = new DBHelper(getContext(), "db.db", null, 1);

        view = inflater.inflate(R.layout.fragment3, container, false);

        singleSelectToggleGroup = (SingleSelectToggleGroup) view.findViewById(R.id.group_choices);

        singleSelectToggleGroup2 = (SingleSelectToggleGroup) view.findViewById(R.id.group_choices2);

        scrollView_start = (NestedScrollView) view.findViewById(R.id.school_start);
        scrollView_arrive = (NestedScrollView) view.findViewById(R.id.school_arrive);

        refresh_Layout3 = (SwipeRefreshLayout) view.findViewById(R.id.refresh_Layout3);

        scroll_vertical_layout1 = (LinearLayout) view.findViewById(R.id.scroll_vertical_layout1);
        scroll_vertical_layout2 = (LinearLayout) view.findViewById(R.id.scroll_vertical_layout2);

        asyncDialog = new ProgressDialog(view.getContext());
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("loading...");
        asyncDialog.setCanceledOnTouchOutside(false);

        task = new JAT();
        task.execute();

        re_swipe2();

        scroll_log();

        current_day();

//        check_butt(weekDay);
//        singleSelectToggleGroup2.clearCheck();
//        singleSelectToggleGroup2.check(R.id.choice_osan);
//        db_save_arrary_list();
//        day_select();
//        goal_select();

        return view;

    }

    //인터넷 연결상태 확인
    private static int getConnectivityStatus(Context context){ //해당 context의 서비스를 사용하기위해서 context객체를 받는다.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){//쓰리지나 LTE로 연결된것(모바일을 뜻한다.)
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){//와이파이 연결된것
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }


    //스레드 크롤링
    private class JAT extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

            asyncDialog.show();
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... strings) {


            //시간 측청
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
//            String formatDate = sdfNow.format(date);
//            System.out.println(formatDate);


            System.out.println("스레드 시작");
            //System.out.println(dbhelper.read_boolean());
            //System.out.println(dbhelper.table_exists());
            //System.out.println(dbhelper.read_boolean().equals("true") && dbhelper.table_exists());

            while (getConnectivityStatus(view.getContext()) == TYPE_NOT_CONNECTED && !dbhelper.table_exists()) {


                asyncDialog.setMessage("인터넷 연결 확인");
                try {

                    Thread.sleep(3000);

                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }


            }

            asyncDialog.setMessage("loading...");


            if (dbhelper.read_boolean().equals("true") && dbhelper.table_exists()) {
                db_save_arrary_list();
            } else {
                System.out.println("크롤링 시작");
                if (dbhelper.table_exists()) {
                    dbhelper.droptable();
                }
                dbhelper.createtable();

                try {
                    Document doc = Jsoup.connect(url_day_cheonan).get();

                    Elements table = doc.select("div.table01 td:eq(2)");
                    Elements table3 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_cheonan_rev.add(n[0]);
                        }
                    }
                    for (int i = 0; i < day_cheonan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.insert(day_cheonan_rev.get(i));
                    }
                    dbhelper.insert("end");
                    System.out.println(day_cheonan_rev.size());
                    System.out.println("평일 천안 역");

                    for (Element e : table) {
                        if (!e.text().contains("X")) {
                            String n[] = (e.text()).split("\\(");
                            fri_cheonan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= fri_cheonan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_cheonan_rev.get(i - 1), i, "cheonan_fri_rev");
                    }
                    System.out.println(fri_cheonan_rev.size());
                    System.out.println("금 천안 역");

                    for (Element e : table3) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_cheonan.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= day_cheonan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(day_cheonan.get(i - 1), i, "cheonan_day");
                    }
                    System.out.println(day_cheonan.size());
                    System.out.println("평일 천안");

                    for (Element e : table3) {
                        if (!e.text().contains("X")) {
                            fri_cheonan.add(e.text());
                        }
                    }
                    for (int i = 1; i <= fri_cheonan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_cheonan.get(i - 1), i, "cheonan_fri");
                    }
                    System.out.println(fri_cheonan.size());
                    System.out.println("금 천안");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_day_terminal).get();

                    Elements table = doc.select("div.table01 td:eq(2)");
                    Elements table2 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_terminal_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= day_terminal_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(day_terminal_rev.get(i - 1), i, "terminal_day_rev");
                    }
                    System.out.println(day_terminal_rev.size());
                    System.out.println("평일 터미널 역");

                    for (Element e : table) {
                        if (!e.text().contains("X")) {
                            String n[] = (e.text()).split("\\(");
                            fri_terminal_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= fri_terminal_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_terminal_rev.get(i - 1), i, "terminal_fri_rev");
                    }
                    System.out.println(fri_terminal_rev.size());
                    System.out.println("금 터미널 역");

                    for (Element e : table2) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_terminal.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= day_terminal.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(day_terminal.get(i - 1), i, "terminal_day");
                    }
                    System.out.println(day_terminal.size());
                    System.out.println("평일 터미널");
                    for (Element e : table2) {
                        if (!e.text().contains("X")) {
                            fri_terminal.add(e.text());
                        }
                    }
                    for (int i = 1; i <= fri_terminal.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_terminal.get(i - 1), i, "terminal_fri");
                    }
                    System.out.println(fri_terminal.size());
                    System.out.println("금 터미널");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_day_asan).get();

                    Elements table = doc.select("div.table01 td:eq(2)");
                    Elements table2 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_asan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= day_asan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(day_asan_rev.get(i - 1), i, "asan_day_rev");
                    }
                    System.out.println(day_asan_rev.size());
                    System.out.println("평일 아산 역");

                    for (Element e : table) {
                        if (!e.text().contains("X")) {
                            String n[] = (e.text()).split("\\(");
                            fri_asan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= fri_asan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_asan_rev.get(i - 1), i, "asan_fri_rev");
                    }
                    System.out.println(fri_asan_rev.size());
                    System.out.println("금 아산 역");

                    for (Element e : table2) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            day_asan.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= day_asan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(day_asan.get(i - 1), i, "asan_day");
                    }
                    System.out.println(day_asan.size());
                    System.out.println("평일 아산");

                    for (Element e : table2) {
                        if (!e.text().contains("X")) {
                            fri_asan.add(e.text());
                        }
                    }
                    for (int i = 1; i <= fri_asan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(fri_asan.get(i - 1), i, "asan_fri");
                    }
                    System.out.println(fri_asan.size());
                    System.out.println("금 아산");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_sat_cheonan_asan).get();

                    Elements table = doc.select("div.table01 td:eq(3)");
                    Elements table2 = doc.select("div.table01 td:eq(4)");
                    Elements table3 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sat_cheonan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sat_cheonan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sat_cheonan_rev.get(i - 1), i, "cheonan_sat_rev");
                    }
                    System.out.println(sat_cheonan_rev.size());
                    System.out.println("토 천안 역");

                    for (Element e : table2) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sat_asan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sat_asan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sat_asan_rev.get(i - 1), i, "asan_sat_rev");
                    }
                    System.out.println(sat_asan_rev.size());
                    System.out.println("토 아산 역");

                    for (Element e : table3) {
                        sat_cheonan_asan.add(e.text());
                    }
                    for (int i = 1; i <= sat_cheonan_asan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sat_cheonan_asan.get(i - 1), i, "cheonan_asan_sat");
                    }
                    System.out.println(sat_cheonan_asan.size());
                    System.out.println("토 천안아산");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_sat_terminal).get();

                    Elements table = doc.select("div.table01 td:eq(2)");
                    Elements table2 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sat_terminal_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sat_terminal_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sat_terminal_rev.get(i - 1), i, "terminal_sat_rev");
                    }
                    System.out.println(sat_terminal_rev.size());
                    System.out.println("토 터미널 역");

                    for (Element e : table2) {
                        sat_terminal.add(e.text());
                    }
                    for (int i = 1; i <= sat_terminal.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sat_terminal.get(i - 1), i, "terminal_sat");
                    }
                    System.out.println(sat_terminal.size());
                    System.out.println("토 터미널");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_sun_cheonan_asan).get();

                    Elements table = doc.select("div.table01 td:eq(3)");
                    Elements table2 = doc.select("div.table01 td:eq(4)");
                    Elements table3 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sun_cheonan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sun_cheonan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sun_cheonan_rev.get(i - 1), i, "cheonan_sun_rev");
                    }
                    System.out.println(sun_cheonan_rev.size());
                    System.out.println("일 천안 역");

                    for (Element e : table2) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sun_asan_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sun_asan_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sun_asan_rev.get(i - 1), i, "asan_sun_rev");
                    }
                    System.out.println(sun_asan_rev.size());
                    System.out.println("일 아산 역");

                    for (Element e : table3) {
                        sun_cheonan_asan.add(e.text());
                    }
                    for (int i = 1; i <= sun_cheonan_asan.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sun_cheonan_asan.get(i - 1), i, "cheonan_asan_sun");
                    }
                    System.out.println(sun_cheonan_asan.size());
                    System.out.println("일 천안아산");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Document doc = Jsoup.connect(url_sun_terminal).get();

                    Elements table = doc.select("div.table01 td:eq(2)");
                    Elements table2 = doc.select("div.table01 td:eq(1)");

                    for (Element e : table) {
                        if (!e.text().equals("X")) {
                            String n[] = (e.text()).split("\\(");
                            sun_terminal_rev.add(n[0]);
                        }
                    }
                    for (int i = 1; i <= sun_terminal_rev.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sun_terminal_rev.get(i - 1), i, "terminal_sun_rev");
                    }
                    System.out.println(sun_terminal_rev.size());
                    System.out.println("일 터미널 역");

                    for (Element e : table2) {
                        sun_terminal.add(e.text());
                    }
                    for (int i = 1; i <= sun_terminal.size(); i++) {
                        System.out.println("포문");
                        dbhelper.update(sun_terminal.get(i - 1), i, "terminal_sun");
                    }
                    System.out.println(sun_terminal.size());
                    System.out.println("일 터미널");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            //측정 종료
//            long now1 = System.currentTimeMillis();
//            Date date1 = new Date(now1);
//            SimpleDateFormat sdfNow1 = new SimpleDateFormat("HH:mm:ss");
//            String formatDate1 = sdfNow1.format(date1);
//            System.out.println(formatDate1);
            System.out.println(dbhelper.read_boolean());
            dbhelper.true_boolean();
            System.out.println(dbhelper.read_boolean());
            System.out.println("쓰레드 종료");


            return null;
        }


        @Override
        protected void onPostExecute(Void s) {

            super.onPostExecute(s);

            asyncDialog.dismiss();

            check_butt(weekDay);
            singleSelectToggleGroup2.clearCheck();
            singleSelectToggleGroup2.check(R.id.choice_osan);
            day_select();
            goal_select();

        }

    }


    //db데이터를 arraylist에 저장
    private void db_save_arrary_list() {
        for (int column = 1; column < 23; column++) {
            //22개의 Arraylist에 모두 값을 넣어준다 column은 x좌표, id는 y좌표로 생각하면 편함
            for (int id = 1; !dbhelper.read(id, column).equals("end"); id++) {
                //read한 값이 가장 마지막 값인 null을 만나면 반환되는 "end"가 아닐 때 까지 반복적으로 실행
                System.out.println(id);
                System.out.println(dbhelper.read(id, column));
                if (column == 1) {
                    day_cheonan_rev.add(dbhelper.read(id, column));
                } else if (column == 2) {
                    day_terminal_rev.add(dbhelper.read(id, column));
                } else if (column == 3) {
                    day_asan_rev.add(dbhelper.read(id, column));
                } else if (column == 4) {
                    fri_cheonan_rev.add(dbhelper.read(id, column));
                } else if (column == 5) {
                    fri_asan_rev.add(dbhelper.read(id, column));
                } else if (column == 6) {
                    fri_terminal_rev.add(dbhelper.read(id, column));
                } else if (column == 7) {
                    sat_cheonan_rev.add(dbhelper.read(id, column));
                } else if (column == 8) {
                    sat_asan_rev.add(dbhelper.read(id, column));
                } else if (column == 9) {
                    sat_terminal_rev.add(dbhelper.read(id, column));
                } else if (column == 10) {
                    sun_cheonan_rev.add(dbhelper.read(id, column));
                } else if (column == 11) {
                    sun_asan_rev.add(dbhelper.read(id, column));
                } else if (column == 12) {
                    sun_terminal_rev.add(dbhelper.read(id, column));
                } else if (column == 13) {
                    day_terminal.add(dbhelper.read(id, column));
                } else if (column == 14) {
                    day_asan.add(dbhelper.read(id, column));
                } else if (column == 15) {
                    day_cheonan.add(dbhelper.read(id, column));
                } else if (column == 16) {
                    fri_terminal.add(dbhelper.read(id, column));
                } else if (column == 17) {
                    fri_asan.add(dbhelper.read(id, column));
                } else if (column == 18) {
                    fri_cheonan.add(dbhelper.read(id, column));
                } else if (column == 19) {
                    sat_cheonan_asan.add(dbhelper.read(id, column));
                } else if (column == 20) {
                    sat_terminal.add(dbhelper.read(id, column));
                } else if (column == 21) {
                    sun_cheonan_asan.add(dbhelper.read(id, column));
                } else {
                    sun_terminal.add(dbhelper.read(id, column));
                }
            }
        }
    }


    public void scroll_log() {
        scrollView_start.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                double scrollViewHeight = scrollView_start.getChildAt(0).getBottom() - scrollView_start.getHeight();
                double getScrollY = scrollView_start.getScrollY();
                double scrollPosition = (getScrollY / scrollViewHeight) * 100d;
                Log.i("scrollview", "scroll Percent Y: " + (int) scrollPosition);
            }
        });
    }

    //당겨서 새로고침
    public void re_swipe2() {
        refresh_Layout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        current_day();
                        check_butt(weekDay);

                        refresh_Layout3.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    //목적지 선택
    public void goal_select() {

        singleSelectToggleGroup2.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                ArrayList<String> empty_array1 = new ArrayList<>();
                ArrayList<String> empty_array2 = new ArrayList<>();

                scroll_vertical_layout1.removeAllViews();
                scroll_vertical_layout2.removeAllViews();

                if (!empty_array1.isEmpty()) {
                    empty_array1.clear();
                }

                if (!empty_array2.isEmpty()) {
                    empty_array2.clear();
                }


                switch (checkedId) {
                    case R.id.choice_osan:

                        if (singleSelectToggleGroup.getCheckedId() == R.id.choice_e) {
                            empty_array1.addAll(fri_asan);
                            empty_array2.addAll(fri_asan_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_f) {
                            empty_array1.addAll(sat_cheonan_asan);
                            empty_array2.addAll(sat_asan_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_g) {
                            empty_array1.addAll(sun_cheonan_asan);
                            empty_array2.addAll(sun_asan_rev);
                        } else {
                            empty_array1.addAll(day_asan);
                            empty_array2.addAll(day_asan_rev);
                        }

                        int Textview_num1 = empty_array1.size();
                        int Textview_num2 = empty_array2.size();

                        textViews1 = new TextView[Textview_num1];
                        textViews2 = new TextView[Textview_num2];

                        for (int i = 0; i < Textview_num1; i++) {
                            textViews1[i] = new TextView(getActivity());
                            textViews1[i].setText(empty_array1.get(i));
                            textViews1[i].setGravity(Gravity.CENTER);
                            textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews1[i].setTextSize(25);
                            textViews1[i].setHeight(190);

                            scroll_vertical_layout1.addView(textViews1[i]);
                        }

                        for (int i = 0; i < Textview_num2; i++) {
                            textViews2[i] = new TextView(getActivity());
                            textViews2[i].setText(empty_array2.get(i));
                            textViews2[i].setGravity(Gravity.CENTER);
                            textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews2[i].setTextSize(25);
                            textViews2[i].setHeight(190);

                            scroll_vertical_layout2.addView(textViews2[i]);
                        }


                        if (singleSelectToggleGroup.getCheckedId() == curr_Day) {
                            int x = current_time(empty_array1);
                            textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout1.removeViewAt(x);
                            scroll_vertical_layout1.addView(textViews1[x], x);

                            int y = current_time(empty_array2);
                            textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout2.removeViewAt(y);
                            scroll_vertical_layout2.addView(textViews2[y], y);

                            scroll_ani(x, y);
                        } else {
                            scroll_set();
                        }
                        break;
                    case R.id.choice_Cheonan:

                        if (singleSelectToggleGroup.getCheckedId() == R.id.choice_e) {
                            empty_array1.addAll(fri_cheonan);
                            empty_array2.addAll(fri_cheonan_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_f) {
                            empty_array1.addAll(sat_cheonan_asan);
                            empty_array2.addAll(sat_cheonan_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_g) {
                            empty_array1.addAll(sun_cheonan_asan);
                            empty_array2.addAll(sun_cheonan_rev);
                        } else {
                            empty_array1.addAll(day_cheonan);
                            empty_array2.addAll(day_cheonan_rev);
                        }

                        int Textview_num3 = empty_array1.size();
                        int Textview_num4 = empty_array2.size();

                        textViews1 = new TextView[Textview_num3];
                        textViews2 = new TextView[Textview_num4];

                        for (int i = 0; i < Textview_num3; i++) {
                            textViews1[i] = new TextView(getActivity());
                            textViews1[i].setText(empty_array1.get(i));
                            textViews1[i].setGravity(Gravity.CENTER);
                            textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews1[i].setTextSize(25);
                            textViews1[i].setHeight(190);

                            scroll_vertical_layout1.addView(textViews1[i]);
                        }

                        for (int i = 0; i < Textview_num4; i++) {
                            textViews2[i] = new TextView(getActivity());
                            textViews2[i].setText(empty_array2.get(i));
                            textViews2[i].setGravity(Gravity.CENTER);
                            textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews2[i].setTextSize(25);
                            textViews2[i].setHeight(190);

                            scroll_vertical_layout2.addView(textViews2[i]);
                        }
                        if (singleSelectToggleGroup.getCheckedId() == curr_Day) {
                            int x = current_time(empty_array1);
                            textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout1.removeViewAt(x);
                            scroll_vertical_layout1.addView(textViews1[x], x);

                            int y = current_time(empty_array2);
                            textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout2.removeViewAt(y);
                            scroll_vertical_layout2.addView(textViews2[y], y);

                            scroll_ani(x, y);
                        } else {
                            scroll_set();
                        }
                        break;
                    case R.id.choice_Cheonan_terminal:

                        if (singleSelectToggleGroup.getCheckedId() == R.id.choice_e) {
                            empty_array1.addAll(fri_terminal);
                            empty_array2.addAll(fri_terminal_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_f) {
                            empty_array1.addAll(sat_terminal);
                            empty_array2.addAll(sat_terminal_rev);
                        } else if (singleSelectToggleGroup.getCheckedId() == R.id.choice_g) {
                            empty_array1.addAll(sun_terminal);
                            empty_array2.addAll(sun_terminal_rev);
                        } else {
                            empty_array1.addAll(day_terminal);
                            empty_array2.addAll(day_terminal_rev);
                        }

                        int Textview_num5 = empty_array1.size();
                        int Textview_num6 = empty_array2.size();

                        textViews1 = new TextView[Textview_num5];
                        textViews2 = new TextView[Textview_num6];

                        for (int i = 0; i < Textview_num5; i++) {
                            textViews1[i] = new TextView(getActivity());
                            textViews1[i].setText(empty_array1.get(i));
                            textViews1[i].setGravity(Gravity.CENTER);
                            textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews1[i].setTextSize(25);
                            textViews1[i].setHeight(190);

                            scroll_vertical_layout1.addView(textViews1[i]);
                        }

                        for (int i = 0; i < Textview_num6; i++) {
                            textViews2[i] = new TextView(getActivity());
                            textViews2[i].setText(empty_array2.get(i));
                            textViews2[i].setGravity(Gravity.CENTER);
                            textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                            textViews2[i].setTextSize(25);
                            textViews2[i].setHeight(190);

                            scroll_vertical_layout2.addView(textViews2[i]);
                        }


                        if (singleSelectToggleGroup.getCheckedId() == curr_Day) {
                            int x = current_time(empty_array1);
                            textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout1.removeViewAt(x);
                            scroll_vertical_layout1.addView(textViews1[x], x);

                            int y = current_time(empty_array2);
                            textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                            scroll_vertical_layout2.removeViewAt(y);
                            scroll_vertical_layout2.addView(textViews2[y], y);

                            scroll_ani(x, y);
                        } else {
                            scroll_set();
                        }
                        break;
                }
            }
        });
    }

    //리스트 초기화
    public void array_reset() {
        day_asan.clear();
        day_asan_rev.clear();
        day_cheonan.clear();
        day_cheonan_rev.clear();
        day_terminal.clear();
        day_terminal_rev.clear();
        fri_asan.clear();
        fri_asan_rev.clear();
        fri_cheonan.clear();
        fri_cheonan_rev.clear();
        fri_terminal.clear();
        fri_terminal_rev.clear();
        sat_asan_rev.clear();
        sat_cheonan_asan.clear();
        sat_cheonan_rev.clear();
        sat_terminal.clear();
        sat_terminal_rev.clear();
        sun_asan_rev.clear();
        sun_cheonan_asan.clear();
        sun_cheonan_rev.clear();
        sun_terminal.clear();
        sun_terminal_rev.clear();
    }

    //요일 선택
    public void day_select() {
        singleSelectToggleGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                scroll_vertical_layout1.removeAllViews();
                scroll_vertical_layout2.removeAllViews();

                ArrayList<String> empty_array3 = new ArrayList<>();
                ArrayList<String> empty_array4 = new ArrayList<>();

                scroll_vertical_layout1.removeAllViews();
                scroll_vertical_layout2.removeAllViews();

                if (!empty_array3.isEmpty()) {
                    empty_array3.clear();
                }

                if (!empty_array4.isEmpty()) {
                    empty_array4.clear();
                }

                if ((checkedId == R.id.choice_a)) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(day_asan);
                        empty_array4.addAll(day_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(day_cheonan);
                        empty_array4.addAll(day_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(day_terminal);
                        empty_array4.addAll(day_terminal_rev);
                    }

                    int Textview_num3 = empty_array3.size();
                    int Textview_num4 = empty_array4.size();

                    textViews1 = new TextView[Textview_num3];
                    textViews2 = new TextView[Textview_num4];

                    for (int i = 0; i < Textview_num3; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num4; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_a == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }

                }
                if ((checkedId == R.id.choice_b)) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(day_asan);
                        empty_array4.addAll(day_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(day_cheonan);
                        empty_array4.addAll(day_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(day_terminal);
                        empty_array4.addAll(day_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];

                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_b == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }

                }
                if ((checkedId == R.id.choice_c)) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(day_asan);
                        empty_array4.addAll(day_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(day_cheonan);
                        empty_array4.addAll(day_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(day_terminal);
                        empty_array4.addAll(day_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];

                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_c == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }
                }
                if ((checkedId == R.id.choice_d)) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(day_asan);
                        empty_array4.addAll(day_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(day_cheonan);
                        empty_array4.addAll(day_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(day_terminal);
                        empty_array4.addAll(day_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];

                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_d == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }
                }
                if (checkedId == R.id.choice_e) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(fri_asan);
                        empty_array4.addAll(fri_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(fri_cheonan);
                        empty_array4.addAll(fri_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(fri_terminal);
                        empty_array4.addAll(fri_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];

                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_e == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }

                }
                if (checkedId == R.id.choice_f) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(sat_cheonan_asan);
                        empty_array4.addAll(sat_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(sat_cheonan_asan);
                        empty_array4.addAll(sat_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(sat_terminal);
                        empty_array4.addAll(sat_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];

                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_f == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);
                    } else {
                        scroll_set();
                    }
                }
                if (checkedId == R.id.choice_g) {
                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_osan) {
                        empty_array3.addAll(sun_cheonan_asan);
                        empty_array4.addAll(sun_asan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan) {
                        empty_array3.addAll(sun_cheonan_asan);
                        empty_array4.addAll(sun_cheonan_rev);
                    }

                    if (singleSelectToggleGroup2.getCheckedId() == R.id.choice_Cheonan_terminal) {
                        empty_array3.addAll(sun_terminal);
                        empty_array4.addAll(sun_terminal_rev);
                    }

                    int Textview_num1 = empty_array3.size();
                    int Textview_num2 = empty_array4.size();

                    textViews1 = new TextView[Textview_num1];
                    textViews2 = new TextView[Textview_num2];


                    for (int i = 0; i < Textview_num1; i++) {
                        textViews1[i] = new TextView(getActivity());
                        textViews1[i].setText(empty_array3.get(i));
                        textViews1[i].setGravity(Gravity.CENTER);
                        textViews1[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews1[i].setTextSize(25);
                        textViews1[i].setHeight(190);

                        scroll_vertical_layout1.addView(textViews1[i]);
                    }

                    for (int i = 0; i < Textview_num2; i++) {
                        textViews2[i] = new TextView(getActivity());
                        textViews2[i].setText(empty_array4.get(i));
                        textViews2[i].setGravity(Gravity.CENTER);
                        textViews2[i].setTextColor(Color.rgb(169, 169, 169));
                        textViews2[i].setTextSize(25);
                        textViews2[i].setHeight(190);

                        scroll_vertical_layout2.addView(textViews2[i]);
                    }

                    if (R.id.choice_g == curr_Day) {
                        int x = current_time(empty_array3);
                        textViews1[x].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout1.removeViewAt(x);
                        scroll_vertical_layout1.addView(textViews1[x], x);

                        int y = current_time(empty_array4);
                        textViews2[y].setTextColor(Color.rgb(248, 91, 78));
                        scroll_vertical_layout2.removeViewAt(y);
                        scroll_vertical_layout2.addView(textViews2[y], y);

                        scroll_ani(x, y);

                    } else {
                        scroll_set();
                    }

                }


            }
        });
    }

    //스크롤 애니
    public void scroll_ani(final int x, final int y) {
        scrollView_start.post(new Runnable() {
            @Override
            public void run() {

                scrollView_start.scrollTo(0, 0);
                scrollView_arrive.scrollTo(0, 0);
                //index가 2보다 클경우
                ObjectAnimator.ofInt(scrollView_start, "scrollY", (x - 2) * 190).setDuration(250).start();

                ObjectAnimator.ofInt(scrollView_arrive, "scrollY", (y - 2) * 190).setDuration(250).start();
            }
        });

    }


    @Override
    public void onResume() {

        System.out.println("온리줌");


        super.onResume();

    }

    // onPause()일때 즉 사용자가 해당 Fragemnt를 떠났을때 호출이 됩니다
    @Override
    public void onPause() {

        System.out.println("온퓨즈");


        super.onPause();
    }

    @Override
    public void onDestroy() {

        array_reset();

        super.onDestroy();
    }

    //현재 요일은 반환하는 함수
    private void current_day() {
        //현재 요일
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.KOREA);
        weekDay = weekdayFormat.format(currentTime);

    }

    //스크롤 초기화
    private void scroll_set() {

        scrollView_start.post(new Runnable() {
            @Override
            public void run() {

                scrollView_start.scrollTo(0, 0);
                scrollView_arrive.scrollTo(0, 0);
            }
        });

    }

    //현재 요일로 버튼 셋
    private void check_butt(String day) {

        singleSelectToggleGroup.clearCheck();

        if (day.equals("월")) {
            singleSelectToggleGroup.check(R.id.choice_a);
            curr_Day = R.id.choice_a;
        } else if (day.equals("화")) {
            singleSelectToggleGroup.check(R.id.choice_b);
            curr_Day = R.id.choice_b;
        } else if (day.equals("수")) {
            singleSelectToggleGroup.check(R.id.choice_c);
            curr_Day = R.id.choice_c;
        } else if (day.equals("목")) {
            singleSelectToggleGroup.check(R.id.choice_d);
            curr_Day = R.id.choice_d;
        } else if (day.equals("금")) {
            singleSelectToggleGroup.check(R.id.choice_e);
            curr_Day = R.id.choice_e;
        } else if (day.equals("토")) {
            singleSelectToggleGroup.check(R.id.choice_f);
            curr_Day = R.id.choice_f;
        } else if (day.equals("일")) {
            singleSelectToggleGroup.check(R.id.choice_g);
            curr_Day = R.id.choice_g;
        }

    }


    public int current_time(ArrayList<String> arrayList) {
        int i = 0;
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat dateSet = new SimpleDateFormat("HH:mm", Locale.KOREA);
        String time_date = dateSet.format(new Date());
        long result = -1;

        try {
            startDate = dateSet.parse(time_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //--현재시간과 셔틀 시간 비교
        while (true) {
            if (i > (arrayList.size() - 1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(arrayList.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result = endDate.getTime() - startDate.getTime();
            if (result >= 0) {
                break;
            }

            i++;
        }


        System.out.println(i);

        return i;
    }


}