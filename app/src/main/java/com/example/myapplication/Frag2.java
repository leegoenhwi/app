package com.example.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Frag2 extends Fragment {
    private SwipeRefreshLayout refresh_Layout2;
    private String url_num = "http://119.67.32.123:8840/bus_count.php";
    private TextView cheonan_num;
    private TextView cheonan_t_num;
    private TextView osan_num;
    private boolean stopFlag = false;
    private String[] ch;
    private String[] ch_t;
    private String[] osan;
    private JAT_frag2 task;

    private TextView os_text_color;
    private TextView os_text_string;

    private TextView ch_text_color;
    private TextView ch_text_string;

    private TextView ch_t_text_color;
    private TextView ch_t_text_string;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);

        refresh_Layout2 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout2);
        osan_num = (TextView) view.findViewById(R.id.osan_num);
        cheonan_num = (TextView) view.findViewById(R.id.cheonan_num);
        cheonan_t_num = (TextView) view.findViewById(R.id.cheonan_t_num);

        os_text_color = (TextView) view.findViewById(R.id.textView2);
        os_text_string = (TextView) view.findViewById(R.id.textView3);

        ch_text_color  = (TextView) view.findViewById(R.id.textView5);
        ch_text_string = (TextView) view.findViewById(R.id.textView6);

        ch_t_text_color  = (TextView) view.findViewById(R.id.textView8);
        ch_t_text_string  = (TextView) view.findViewById(R.id.textView9);

        //task = new JAT_frag2();

        stopFlag = false;

        re_swipe2();



        return view;
    }



    //당겨서 새로고침
    public void re_swipe2()
    {
        refresh_Layout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                        refresh_Layout2.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();

        //AsyncTask 생성, 실행
        task = new JAT_frag2();
        stopFlag = false;
        task.execute();

        System.out.println("온리줌");
    }

    // onPause()일때 즉 사용자가 해당 Fragemnt를 떠났을때 호출이 됩니다
    @Override
    public void onPause()
    {
        stopFlag = true;
        task.cancel(true);
        System.out.println("온퓨즈");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        System.out.println("온스탐");
        super.onStop();
    }

    //gps 크롤링
    private class JAT_frag2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("온프리익스큐트");

        }

        @Override
        protected Void doInBackground(Void... params) {

            while (!stopFlag) {
                System.out.println("온두인백그라운드");

                Document doc;

                try {

                    doc = Jsoup.connect(url_num).get();


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("온인백그라운드 try catch");

                    try {
                        System.out.println("슬립");
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    System.out.println("컨티뉴");
                    continue;
                }

                Elements line0 = doc.select("div.0");
                Elements line1 = doc.select("div.1");
                Elements line2 = doc.select("div.2");

                osan = line0.text().split(" ");
                ch = line1.text().split(" ");
                ch_t = line2.text().split(" ");


                try {
                    publishProgress();
                    Thread.sleep(5000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            }
            System.out.println("두잉 백그라운드");
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

            System.out.println("프로그레스 업데이트");

            osan_num.setText(osan[0]);
            cheonan_num.setText(ch[0]);
            cheonan_t_num.setText(ch_t[0]);


            if(Integer.parseInt(String.valueOf(osan_num.getText())) < 20)
            {
                os_text_color.setBackgroundColor(Color.rgb(60,174,136));
                os_text_string.setTextColor(Color.rgb(60,174,136));
                os_text_string.setText("쾌적");
                osan_num.setTextColor(Color.rgb(60,174,136));
            }

            if(Integer.parseInt(String.valueOf(cheonan_num.getText())) < 20)
            {
                ch_text_color.setBackgroundColor(Color.rgb(60,174,136));
                ch_text_string.setTextColor(Color.rgb(60,174,136));
                ch_text_string.setText("쾌적");
                cheonan_num.setTextColor(Color.rgb(60,174,136));
            }

            if(Integer.parseInt(String.valueOf(cheonan_t_num.getText())) < 20)
            {
                ch_t_text_color.setBackgroundColor(Color.rgb(60,174,136));
                ch_t_text_string.setTextColor(Color.rgb(60,174,136));
                ch_t_text_string.setText("쾌적");
                cheonan_t_num.setTextColor(Color.rgb(60,174,136));
            }

            if(Integer.parseInt(String.valueOf(osan_num.getText())) >= 20 && Integer.parseInt(String.valueOf(osan_num.getText())) < 35)
            {
                os_text_color.setBackgroundColor(Color.rgb(242,190,34));
                os_text_string.setTextColor(Color.rgb(242,190,34));
                os_text_string.setText("보통");
                osan_num.setTextColor(Color.rgb(242,190,34));
            }

            if(Integer.parseInt(String.valueOf(cheonan_num.getText())) >= 20 && Integer.parseInt(String.valueOf(cheonan_num.getText())) < 35)
            {
                ch_text_color.setBackgroundColor(Color.rgb(242,190,34));
                ch_text_string.setTextColor(Color.rgb(242,190,34));
                ch_text_string.setText("보통");
                cheonan_num.setTextColor(Color.rgb(242,190,34));
            }

            if(Integer.parseInt(String.valueOf(cheonan_t_num.getText())) >= 20 && Integer.parseInt(String.valueOf(cheonan_t_num.getText())) < 35)
            {
                ch_t_text_color.setBackgroundColor(Color.rgb(242,190,34));
                ch_t_text_string.setTextColor(Color.rgb(242,190,34));
                ch_t_text_string.setText("보통");
                cheonan_t_num.setTextColor(Color.rgb(242,190,34));
            }


            if(Integer.parseInt(String.valueOf(osan_num.getText())) >= 35)
            {
                os_text_color.setBackgroundColor(Color.rgb(248, 91, 78));
                os_text_string.setTextColor(Color.rgb(248, 91, 78));
                os_text_string.setText("포화");
                osan_num.setTextColor(Color.rgb(248, 91, 78));
            }

            if(Integer.parseInt(String.valueOf(cheonan_num.getText())) >= 35)
            {
                ch_text_color.setBackgroundColor(Color.rgb(248, 91, 78));
                ch_text_string.setTextColor(Color.rgb(248, 91, 78));
                ch_text_string.setText("포화");
                cheonan_num.setTextColor(Color.rgb(248, 91, 78));

            }

            if(Integer.parseInt(String.valueOf(cheonan_t_num.getText())) >= 35)
            {
                ch_t_text_color.setBackgroundColor(Color.rgb(248, 91, 78));
                ch_t_text_string.setTextColor(Color.rgb(248, 91, 78));
                ch_t_text_string.setText("포화");
                cheonan_t_num.setTextColor(Color.rgb(248, 91, 78));
            }



        }


        @Override
        protected void onPostExecute(Void result) {

        }


        // cancel 호출하면 실행 됨
        @Override
        protected void onCancelled() {
            super.onCancelled();
            System.out.println("온 캔슬");
        }



    }
}
