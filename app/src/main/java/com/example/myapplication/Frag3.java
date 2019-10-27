package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Frag3 extends Fragment {
    private View view;
    private TextView start;
    private TextView end;

    private SwipeRefreshLayout refresh_Layout3;
    private SingleSelectToggleGroup singleSelectToggleGroup;
    private SingleSelectToggleGroup singleSelectToggleGroup2;

    private  TextView textView = null;
    private  TextView textView2 = null;

    private NestedScrollView scrollView_start = null;
    private NestedScrollView scrollView_arrive = null;

    private LinearLayout scroll_vertical_layout1 = null;
    private LinearLayout scroll_vertical_layout2 = null;

    private ArrayList<String> asan_cam = null;
    private ArrayList<String> cam_asan = null;

    private ArrayList<String> Cheonan_cam = null;
    private ArrayList<String> cam_Cheonan = null;

    private ArrayList<String> Cheonan_terminal_cam = null;
    private ArrayList<String> cam_Cheonan_terminal = null;

    private TextView[] textViews;

    int curr_Day;

    private String weekDay = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment3, container, false);

        singleSelectToggleGroup = (SingleSelectToggleGroup) view.findViewById(R.id.group_choices);

        singleSelectToggleGroup2 = (SingleSelectToggleGroup) view.findViewById(R.id.group_choices2);

        scrollView_start = (NestedScrollView) view.findViewById(R.id.school_start);
        scrollView_arrive = (NestedScrollView) view.findViewById(R.id.school_arrive);

        refresh_Layout3 = (SwipeRefreshLayout) view.findViewById(R.id.refresh_Layout3);


        scroll_vertical_layout1 = (LinearLayout) view.findViewById(R.id.scroll_vertical_layout1);
        scroll_vertical_layout2 = (LinearLayout) view.findViewById(R.id.scroll_vertical_layout2);


        //textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","      "),TextView.BufferType.SPANNABLE);

       //색칠
        /*Spannable spannable = (Spannable)textView.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), 14, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/


       re_swipe2();
       // goal_select();
       // day_select();
       current_day();

        check_butt(weekDay);

        singleSelectToggleGroup2.check(R.id.choice_osan);

        scroll_view_set(scroll_vertical_layout1);
        scroll_view_set(scroll_vertical_layout2);

        //size_up(3);

        scroll_log();



        return view;

    }

    //Text_view 생성
    public void scroll_view_set(LinearLayout linearLayout)
    {
        int Textview_num = 50;

        textViews = new TextView[Textview_num];

        for(int i = 0;i<Textview_num;i++)
        {
            textViews[i] = new TextView(getActivity());
            textViews[i].setText("11 : 30");
            textViews[i].setGravity(Gravity.CENTER);
            textViews[i].setTextColor(Color.rgb(169,169,169));
            textViews[i].setTextSize(25);
            textViews[i].setHeight(190);
            textViews[i].setId(i);
            linearLayout.addView(textViews[i]);
        }

        if(linearLayout.getId() == scroll_vertical_layout1.getId()) {
            size_up(10);
        }
        else
        {
            size_up(6);
        }

    }

    //특정 Text_view 크기 색
    public void size_up(int x)
    {
        textViews[x].setTextSize(35);
        textViews[x].setTextColor(Color.rgb(248,91,78));
    }

    //scroll_position_log
    public void scroll_log()
    {
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
    public void re_swipe2()
    {
        refresh_Layout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                       current_day();
                       check_butt(weekDay);

                        refresh_Layout3.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    //목적지 선택
    public void goal_select()
    {
        singleSelectToggleGroup2.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.choice_osan:
                        start.setText("아산역      ->      아산캠퍼스");
                        end.setText("아산캠퍼스      ->      아산역");
                        textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        if(singleSelectToggleGroup.getCheckedId() ==   curr_Day)
                        {
                            //System.out.println(current_Day);
                            current_asan_cam();
                            current_cam_osan();
                        }
                        break;
                    case R.id.choice_Cheonan:
                        start.setText("천안역      ->      아산캠퍼스");
                        end.setText("아산캠퍼스      ->      천안역");
                        textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        if(singleSelectToggleGroup.getCheckedId() ==  curr_Day)
                        {
                            //System.out.println(current_Day);
                            current_Cheonan_cam();
                            current_cam_Cheonan();
                        }
                        break;
                    case R.id.choice_Cheonan_terminal:
                        start.setText("천안터미널      ->      아산캠퍼스");
                        end.setText("아산캠퍼스      ->      천안터미널");
                        textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                        if(singleSelectToggleGroup.getCheckedId() == curr_Day)
                        {
                            //System.out.println(current_Day);
                            current_Cheonan_terminal_cam();
                            current_cam_Cheonan_terminal();
                        }
                        break;
                }
            }
        });
    }

    //리스트 초기화
    public void array_reset()
    {
        if(asan_cam != null) {
            asan_cam.clear();
            cam_asan.clear();

            Cheonan_cam.clear();
            cam_Cheonan.clear();

            Cheonan_terminal_cam.clear();
            cam_Cheonan_terminal.clear();
        }
    }

    //요일 선택
    public void day_select()
    {

       // final String dayday = new String(weekDay);
        array_reset();

        singleSelectToggleGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {

                if( (checkedId == R.id.choice_a))
                {
                        //array_reset();

                        asan_cam = new ArrayList<>();
                        asan_cam.add("08:40");asan_cam.add("08:50");asan_cam.add("08:55");asan_cam.add("09:05");asan_cam.add("09:15");
                        asan_cam.add("09:55");asan_cam.add("10:15");asan_cam.add("10:45");asan_cam.add("11:10");asan_cam.add("11:20");
                        asan_cam.add("11:50");asan_cam.add("12:05");asan_cam.add("12:20");asan_cam.add("12:45");asan_cam.add("13:15");
                        asan_cam.add("13:45");asan_cam.add("14:15");asan_cam.add("14:45");asan_cam.add("15:15");asan_cam.add("15:40");
                        asan_cam.add("15:50");asan_cam.add("16:15");asan_cam.add("16:40");asan_cam.add("16:55");asan_cam.add("17:20");
                        asan_cam.add("17:40");asan_cam.add("17:50");asan_cam.add("18:10");asan_cam.add("18:35");asan_cam.add("18:45");
                        asan_cam.add("19:45");asan_cam.add("20:05");asan_cam.add("20:25");asan_cam.add("20:45");asan_cam.add("21:05");
                        asan_cam.add("21:25");asan_cam.add("21:45");asan_cam.add("22:15");

                        cam_asan = new ArrayList<>();
                        cam_asan.add("08:40");cam_asan.add("09:45");cam_asan.add("10:05");cam_asan.add("10:35");cam_asan.add("11:10");
                        cam_asan.add("11:40");cam_asan.add("11:55");cam_asan.add("12:10");cam_asan.add("12:35");cam_asan.add("13:05");
                        cam_asan.add("13:35");cam_asan.add("14:05");cam_asan.add("14:35");cam_asan.add("15:05");cam_asan.add("15:30");
                        cam_asan.add("15:40");cam_asan.add("16:05");cam_asan.add("16:30");cam_asan.add("16:45");cam_asan.add("17:10");
                        cam_asan.add("17:30");cam_asan.add("17:40");cam_asan.add("18:00");cam_asan.add("18:25");cam_asan.add("18:35");
                        cam_asan.add("19:00");cam_asan.add("19:20");cam_asan.add("19:40");cam_asan.add("20:00");cam_asan.add("20:20");
                        cam_asan.add("20:40");cam_asan.add("21:00");cam_asan.add("21:30");

                        Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:15");Cheonan_cam.add("08:35");Cheonan_cam.add("08:45");Cheonan_cam.add("08:50");Cheonan_cam.add("08:53");
                    Cheonan_cam.add("08:56");Cheonan_cam.add("08:58");Cheonan_cam.add("09:00");Cheonan_cam.add("09:03");Cheonan_cam.add("09:10");
                    Cheonan_cam.add("09:50");Cheonan_cam.add("10:00");Cheonan_cam.add("10:20");Cheonan_cam.add("10:40");Cheonan_cam.add("10:50");
                    Cheonan_cam.add("10:55");Cheonan_cam.add("11:00");Cheonan_cam.add("11:10");Cheonan_cam.add("11:40");Cheonan_cam.add("12:00");
                    Cheonan_cam.add("12:05");Cheonan_cam.add("12:30");Cheonan_cam.add("13:00");Cheonan_cam.add("13:30");Cheonan_cam.add("14:00");
                    Cheonan_cam.add("14:30");Cheonan_cam.add("15:00");Cheonan_cam.add("15:30");Cheonan_cam.add("15:40");Cheonan_cam.add("15:50");
                    Cheonan_cam.add("16:00");Cheonan_cam.add("16:20");Cheonan_cam.add("16:55");Cheonan_cam.add("17:05");Cheonan_cam.add("17:25");
                    Cheonan_cam.add("17:50");Cheonan_cam.add("18:00");Cheonan_cam.add("18:15");Cheonan_cam.add("18:40");Cheonan_cam.add("18:55");
                    Cheonan_cam.add("19:05");Cheonan_cam.add("19:30");Cheonan_cam.add("19:50");Cheonan_cam.add("20:10");Cheonan_cam.add("20:30");
                    Cheonan_cam.add("20:50");Cheonan_cam.add("21:10");Cheonan_cam.add("21:30");Cheonan_cam.add("22:20");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:15");cam_Cheonan.add("09:35");cam_Cheonan.add("09:55");cam_Cheonan.add("10:35");cam_Cheonan.add("11:15");
                    cam_Cheonan.add("11:40");cam_Cheonan.add("12:10");cam_Cheonan.add("12:40");cam_Cheonan.add("13:10");cam_Cheonan.add("13:40");
                    cam_Cheonan.add("14:10");cam_Cheonan.add("14:40");cam_Cheonan.add("15:10");cam_Cheonan.add("15:20");cam_Cheonan.add("15:30");
                    cam_Cheonan.add("15:40");cam_Cheonan.add("16:00");cam_Cheonan.add("16:30");cam_Cheonan.add("16:40");cam_Cheonan.add("17:00");
                    cam_Cheonan.add("17:20");cam_Cheonan.add("17:30");cam_Cheonan.add("17:45");cam_Cheonan.add("18:10");cam_Cheonan.add("18:25");
                    cam_Cheonan.add("18:35");cam_Cheonan.add("19:00");cam_Cheonan.add("19:20");cam_Cheonan.add("19:40");cam_Cheonan.add("20:00");
                    cam_Cheonan.add("20:20");cam_Cheonan.add("20:40");cam_Cheonan.add("21:00");cam_Cheonan.add("21:30");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:10");Cheonan_terminal_cam.add("08:25");Cheonan_terminal_cam.add("08:35");Cheonan_terminal_cam.add("08:40");Cheonan_terminal_cam.add("08:45");
                    Cheonan_terminal_cam.add("08:50");Cheonan_terminal_cam.add("08:55");Cheonan_terminal_cam.add("09:00");Cheonan_terminal_cam.add("09:40");Cheonan_terminal_cam.add("09:55");
                    Cheonan_terminal_cam.add("10:15");Cheonan_terminal_cam.add("10:35");Cheonan_terminal_cam.add("10:50");Cheonan_terminal_cam.add("11:00");Cheonan_terminal_cam.add("11:30");
                    Cheonan_terminal_cam.add("11:45");Cheonan_terminal_cam.add("12:00");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:00");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("14:00");Cheonan_terminal_cam.add("14:30");Cheonan_terminal_cam.add("15:00");Cheonan_terminal_cam.add("15:20");Cheonan_terminal_cam.add("15:40");
                    Cheonan_terminal_cam.add("16:00");Cheonan_terminal_cam.add("16:10");Cheonan_terminal_cam.add("16:30");Cheonan_terminal_cam.add("17:00");Cheonan_terminal_cam.add("17:10");
                    Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("17:50");Cheonan_terminal_cam.add("18:00");Cheonan_terminal_cam.add("18:15");Cheonan_terminal_cam.add("18:40");
                    Cheonan_terminal_cam.add("19:00");Cheonan_terminal_cam.add("19:20");Cheonan_terminal_cam.add("19:40");Cheonan_terminal_cam.add("20:10");Cheonan_terminal_cam.add("20:40");
                    Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("22:00");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("07:45"); cam_Cheonan_terminal.add("08:10"); cam_Cheonan_terminal.add("08:20"); cam_Cheonan_terminal.add("09:10"); cam_Cheonan_terminal.add("09:25");
                    cam_Cheonan_terminal.add("09:45"); cam_Cheonan_terminal.add("10:05"); cam_Cheonan_terminal.add("10:30"); cam_Cheonan_terminal.add("11:00"); cam_Cheonan_terminal.add("11:30");
                    cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("12:30"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("13:30"); cam_Cheonan_terminal.add("14:00");
                    cam_Cheonan_terminal.add("14:30"); cam_Cheonan_terminal.add("14:50"); cam_Cheonan_terminal.add("15:10"); cam_Cheonan_terminal.add("15:30"); cam_Cheonan_terminal.add("15:40");
                    cam_Cheonan_terminal.add("16:00"); cam_Cheonan_terminal.add("16:30"); cam_Cheonan_terminal.add("16:40"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("17:20");
                    cam_Cheonan_terminal.add("17:30"); cam_Cheonan_terminal.add("17:45"); cam_Cheonan_terminal.add("18:10"); cam_Cheonan_terminal.add("18:30"); cam_Cheonan_terminal.add("18:50");
                    cam_Cheonan_terminal.add("19:10"); cam_Cheonan_terminal.add("19:40"); cam_Cheonan_terminal.add("20:10"); cam_Cheonan_terminal.add("20:50"); cam_Cheonan_terminal.add("21:30");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }
                    if(R.id.choice_a == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }

                }

                else if( (checkedId == R.id.choice_b))
                {
                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("08:40");asan_cam.add("08:50");asan_cam.add("08:55");asan_cam.add("09:05");asan_cam.add("09:15");
                    asan_cam.add("09:55");asan_cam.add("10:15");asan_cam.add("10:45");asan_cam.add("11:10");asan_cam.add("11:20");
                    asan_cam.add("11:50");asan_cam.add("12:05");asan_cam.add("12:20");asan_cam.add("12:45");asan_cam.add("13:15");
                    asan_cam.add("13:45");asan_cam.add("14:15");asan_cam.add("14:45");asan_cam.add("15:15");asan_cam.add("15:40");
                    asan_cam.add("15:50");asan_cam.add("16:15");asan_cam.add("16:40");asan_cam.add("16:55");asan_cam.add("17:20");
                    asan_cam.add("17:40");asan_cam.add("17:50");asan_cam.add("18:10");asan_cam.add("18:35");asan_cam.add("18:45");
                    asan_cam.add("19:45");asan_cam.add("20:05");asan_cam.add("20:25");asan_cam.add("20:45");asan_cam.add("21:05");
                    asan_cam.add("21:25");asan_cam.add("21:45");asan_cam.add("22:15");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("08:40");cam_asan.add("09:45");cam_asan.add("10:05");cam_asan.add("10:35");cam_asan.add("11:10");
                    cam_asan.add("11:40");cam_asan.add("11:55");cam_asan.add("12:10");cam_asan.add("12:35");cam_asan.add("13:05");
                    cam_asan.add("13:35");cam_asan.add("14:05");cam_asan.add("14:35");cam_asan.add("15:05");cam_asan.add("15:30");
                    cam_asan.add("15:40");cam_asan.add("16:05");cam_asan.add("16:30");cam_asan.add("16:45");cam_asan.add("17:10");
                    cam_asan.add("17:30");cam_asan.add("17:40");cam_asan.add("18:00");cam_asan.add("18:25");cam_asan.add("18:35");
                    cam_asan.add("19:00");cam_asan.add("19:20");cam_asan.add("19:40");cam_asan.add("20:00");cam_asan.add("20:20");
                    cam_asan.add("20:40");cam_asan.add("21:00");cam_asan.add("21:30");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:15");Cheonan_cam.add("08:35");Cheonan_cam.add("08:45");Cheonan_cam.add("08:50");Cheonan_cam.add("08:53");
                    Cheonan_cam.add("08:56");Cheonan_cam.add("08:58");Cheonan_cam.add("09:00");Cheonan_cam.add("09:03");Cheonan_cam.add("09:10");
                    Cheonan_cam.add("09:50");Cheonan_cam.add("10:00");Cheonan_cam.add("10:20");Cheonan_cam.add("10:40");Cheonan_cam.add("10:50");
                    Cheonan_cam.add("10:55");Cheonan_cam.add("11:00");Cheonan_cam.add("11:10");Cheonan_cam.add("11:40");Cheonan_cam.add("12:00");
                    Cheonan_cam.add("12:05");Cheonan_cam.add("12:30");Cheonan_cam.add("13:00");Cheonan_cam.add("13:30");Cheonan_cam.add("14:00");
                    Cheonan_cam.add("14:30");Cheonan_cam.add("15:00");Cheonan_cam.add("15:30");Cheonan_cam.add("15:40");Cheonan_cam.add("15:50");
                    Cheonan_cam.add("16:00");Cheonan_cam.add("16:20");Cheonan_cam.add("16:55");Cheonan_cam.add("17:05");Cheonan_cam.add("17:25");
                    Cheonan_cam.add("17:50");Cheonan_cam.add("18:00");Cheonan_cam.add("18:15");Cheonan_cam.add("18:40");Cheonan_cam.add("18:55");
                    Cheonan_cam.add("19:05");Cheonan_cam.add("19:30");Cheonan_cam.add("19:50");Cheonan_cam.add("20:10");Cheonan_cam.add("20:30");
                    Cheonan_cam.add("20:50");Cheonan_cam.add("21:10");Cheonan_cam.add("21:30");Cheonan_cam.add("22:20");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:15");cam_Cheonan.add("09:35");cam_Cheonan.add("09:55");cam_Cheonan.add("10:35");cam_Cheonan.add("11:15");
                    cam_Cheonan.add("11:40");cam_Cheonan.add("12:10");cam_Cheonan.add("12:40");cam_Cheonan.add("13:10");cam_Cheonan.add("13:40");
                    cam_Cheonan.add("14:10");cam_Cheonan.add("14:40");cam_Cheonan.add("15:10");cam_Cheonan.add("15:20");cam_Cheonan.add("15:30");
                    cam_Cheonan.add("15:40");cam_Cheonan.add("16:00");cam_Cheonan.add("16:30");cam_Cheonan.add("16:40");cam_Cheonan.add("17:00");
                    cam_Cheonan.add("17:20");cam_Cheonan.add("17:30");cam_Cheonan.add("17:45");cam_Cheonan.add("18:10");cam_Cheonan.add("18:25");
                    cam_Cheonan.add("18:35");cam_Cheonan.add("19:00");cam_Cheonan.add("19:20");cam_Cheonan.add("19:40");cam_Cheonan.add("20:00");
                    cam_Cheonan.add("20:20");cam_Cheonan.add("20:40");cam_Cheonan.add("21:00");cam_Cheonan.add("21:30");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:10");Cheonan_terminal_cam.add("08:25");Cheonan_terminal_cam.add("08:35");Cheonan_terminal_cam.add("08:40");Cheonan_terminal_cam.add("08:45");
                    Cheonan_terminal_cam.add("08:50");Cheonan_terminal_cam.add("08:55");Cheonan_terminal_cam.add("09:00");Cheonan_terminal_cam.add("09:40");Cheonan_terminal_cam.add("09:55");
                    Cheonan_terminal_cam.add("10:15");Cheonan_terminal_cam.add("10:35");Cheonan_terminal_cam.add("10:50");Cheonan_terminal_cam.add("11:00");Cheonan_terminal_cam.add("11:30");
                    Cheonan_terminal_cam.add("11:45");Cheonan_terminal_cam.add("12:00");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:00");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("14:00");Cheonan_terminal_cam.add("14:30");Cheonan_terminal_cam.add("15:00");Cheonan_terminal_cam.add("15:20");Cheonan_terminal_cam.add("15:40");
                    Cheonan_terminal_cam.add("16:00");Cheonan_terminal_cam.add("16:10");Cheonan_terminal_cam.add("16:30");Cheonan_terminal_cam.add("17:00");Cheonan_terminal_cam.add("17:10");
                    Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("17:50");Cheonan_terminal_cam.add("18:00");Cheonan_terminal_cam.add("18:15");Cheonan_terminal_cam.add("18:40");
                    Cheonan_terminal_cam.add("19:00");Cheonan_terminal_cam.add("19:20");Cheonan_terminal_cam.add("19:40");Cheonan_terminal_cam.add("20:10");Cheonan_terminal_cam.add("20:40");
                    Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("22:00");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("07:45"); cam_Cheonan_terminal.add("08:10"); cam_Cheonan_terminal.add("08:20"); cam_Cheonan_terminal.add("09:10"); cam_Cheonan_terminal.add("09:25");
                    cam_Cheonan_terminal.add("09:45"); cam_Cheonan_terminal.add("10:05"); cam_Cheonan_terminal.add("10:30"); cam_Cheonan_terminal.add("11:00"); cam_Cheonan_terminal.add("11:30");
                    cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("12:30"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("13:30"); cam_Cheonan_terminal.add("14:00");
                    cam_Cheonan_terminal.add("14:30"); cam_Cheonan_terminal.add("14:50"); cam_Cheonan_terminal.add("15:10"); cam_Cheonan_terminal.add("15:30"); cam_Cheonan_terminal.add("15:40");
                    cam_Cheonan_terminal.add("16:00"); cam_Cheonan_terminal.add("16:30"); cam_Cheonan_terminal.add("16:40"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("17:20");
                    cam_Cheonan_terminal.add("17:30"); cam_Cheonan_terminal.add("17:45"); cam_Cheonan_terminal.add("18:10"); cam_Cheonan_terminal.add("18:30"); cam_Cheonan_terminal.add("18:50");
                    cam_Cheonan_terminal.add("19:10"); cam_Cheonan_terminal.add("19:40"); cam_Cheonan_terminal.add("20:10"); cam_Cheonan_terminal.add("20:50"); cam_Cheonan_terminal.add("21:30");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }
                    if(R.id.choice_b == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }

                }

                else if( (checkedId == R.id.choice_c))
                {
                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("08:40");asan_cam.add("08:50");asan_cam.add("08:55");asan_cam.add("09:05");asan_cam.add("09:15");
                    asan_cam.add("09:55");asan_cam.add("10:15");asan_cam.add("10:45");asan_cam.add("11:10");asan_cam.add("11:20");
                    asan_cam.add("11:50");asan_cam.add("12:05");asan_cam.add("12:20");asan_cam.add("12:45");asan_cam.add("13:15");
                    asan_cam.add("13:45");asan_cam.add("14:15");asan_cam.add("14:45");asan_cam.add("15:15");asan_cam.add("15:40");
                    asan_cam.add("15:50");asan_cam.add("16:15");asan_cam.add("16:40");asan_cam.add("16:55");asan_cam.add("17:20");
                    asan_cam.add("17:40");asan_cam.add("17:50");asan_cam.add("18:10");asan_cam.add("18:35");asan_cam.add("18:45");
                    asan_cam.add("19:45");asan_cam.add("20:05");asan_cam.add("20:25");asan_cam.add("20:45");asan_cam.add("21:05");
                    asan_cam.add("21:25");asan_cam.add("21:45");asan_cam.add("22:15");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("08:40");cam_asan.add("09:45");cam_asan.add("10:05");cam_asan.add("10:35");cam_asan.add("11:10");
                    cam_asan.add("11:40");cam_asan.add("11:55");cam_asan.add("12:10");cam_asan.add("12:35");cam_asan.add("13:05");
                    cam_asan.add("13:35");cam_asan.add("14:05");cam_asan.add("14:35");cam_asan.add("15:05");cam_asan.add("15:30");
                    cam_asan.add("15:40");cam_asan.add("16:05");cam_asan.add("16:30");cam_asan.add("16:45");cam_asan.add("17:10");
                    cam_asan.add("17:30");cam_asan.add("17:40");cam_asan.add("18:00");cam_asan.add("18:25");cam_asan.add("18:35");
                    cam_asan.add("19:00");cam_asan.add("19:20");cam_asan.add("19:40");cam_asan.add("20:00");cam_asan.add("20:20");
                    cam_asan.add("20:40");cam_asan.add("21:00");cam_asan.add("21:30");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:15");Cheonan_cam.add("08:35");Cheonan_cam.add("08:45");Cheonan_cam.add("08:50");Cheonan_cam.add("08:53");
                    Cheonan_cam.add("08:56");Cheonan_cam.add("08:58");Cheonan_cam.add("09:00");Cheonan_cam.add("09:03");Cheonan_cam.add("09:10");
                    Cheonan_cam.add("09:50");Cheonan_cam.add("10:00");Cheonan_cam.add("10:20");Cheonan_cam.add("10:40");Cheonan_cam.add("10:50");
                    Cheonan_cam.add("10:55");Cheonan_cam.add("11:00");Cheonan_cam.add("11:10");Cheonan_cam.add("11:40");Cheonan_cam.add("12:00");
                    Cheonan_cam.add("12:05");Cheonan_cam.add("12:30");Cheonan_cam.add("13:00");Cheonan_cam.add("13:30");Cheonan_cam.add("14:00");
                    Cheonan_cam.add("14:30");Cheonan_cam.add("15:00");Cheonan_cam.add("15:30");Cheonan_cam.add("15:40");Cheonan_cam.add("15:50");
                    Cheonan_cam.add("16:00");Cheonan_cam.add("16:20");Cheonan_cam.add("16:55");Cheonan_cam.add("17:05");Cheonan_cam.add("17:25");
                    Cheonan_cam.add("17:50");Cheonan_cam.add("18:00");Cheonan_cam.add("18:15");Cheonan_cam.add("18:40");Cheonan_cam.add("18:55");
                    Cheonan_cam.add("19:05");Cheonan_cam.add("19:30");Cheonan_cam.add("19:50");Cheonan_cam.add("20:10");Cheonan_cam.add("20:30");
                    Cheonan_cam.add("20:50");Cheonan_cam.add("21:10");Cheonan_cam.add("21:30");Cheonan_cam.add("22:20");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:15");cam_Cheonan.add("09:35");cam_Cheonan.add("09:55");cam_Cheonan.add("10:35");cam_Cheonan.add("11:15");
                    cam_Cheonan.add("11:40");cam_Cheonan.add("12:10");cam_Cheonan.add("12:40");cam_Cheonan.add("13:10");cam_Cheonan.add("13:40");
                    cam_Cheonan.add("14:10");cam_Cheonan.add("14:40");cam_Cheonan.add("15:10");cam_Cheonan.add("15:20");cam_Cheonan.add("15:30");
                    cam_Cheonan.add("15:40");cam_Cheonan.add("16:00");cam_Cheonan.add("16:30");cam_Cheonan.add("16:40");cam_Cheonan.add("17:00");
                    cam_Cheonan.add("17:20");cam_Cheonan.add("17:30");cam_Cheonan.add("17:45");cam_Cheonan.add("18:10");cam_Cheonan.add("18:25");
                    cam_Cheonan.add("18:35");cam_Cheonan.add("19:00");cam_Cheonan.add("19:20");cam_Cheonan.add("19:40");cam_Cheonan.add("20:00");
                    cam_Cheonan.add("20:20");cam_Cheonan.add("20:40");cam_Cheonan.add("21:00");cam_Cheonan.add("21:30");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:10");Cheonan_terminal_cam.add("08:25");Cheonan_terminal_cam.add("08:35");Cheonan_terminal_cam.add("08:40");Cheonan_terminal_cam.add("08:45");
                    Cheonan_terminal_cam.add("08:50");Cheonan_terminal_cam.add("08:55");Cheonan_terminal_cam.add("09:00");Cheonan_terminal_cam.add("09:40");Cheonan_terminal_cam.add("09:55");
                    Cheonan_terminal_cam.add("10:15");Cheonan_terminal_cam.add("10:35");Cheonan_terminal_cam.add("10:50");Cheonan_terminal_cam.add("11:00");Cheonan_terminal_cam.add("11:30");
                    Cheonan_terminal_cam.add("11:45");Cheonan_terminal_cam.add("12:00");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:00");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("14:00");Cheonan_terminal_cam.add("14:30");Cheonan_terminal_cam.add("15:00");Cheonan_terminal_cam.add("15:20");Cheonan_terminal_cam.add("15:40");
                    Cheonan_terminal_cam.add("16:00");Cheonan_terminal_cam.add("16:10");Cheonan_terminal_cam.add("16:30");Cheonan_terminal_cam.add("17:00");Cheonan_terminal_cam.add("17:10");
                    Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("17:50");Cheonan_terminal_cam.add("18:00");Cheonan_terminal_cam.add("18:15");Cheonan_terminal_cam.add("18:40");
                    Cheonan_terminal_cam.add("19:00");Cheonan_terminal_cam.add("19:20");Cheonan_terminal_cam.add("19:40");Cheonan_terminal_cam.add("20:10");Cheonan_terminal_cam.add("20:40");
                    Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("22:00");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("07:45"); cam_Cheonan_terminal.add("08:10"); cam_Cheonan_terminal.add("08:20"); cam_Cheonan_terminal.add("09:10"); cam_Cheonan_terminal.add("09:25");
                    cam_Cheonan_terminal.add("09:45"); cam_Cheonan_terminal.add("10:05"); cam_Cheonan_terminal.add("10:30"); cam_Cheonan_terminal.add("11:00"); cam_Cheonan_terminal.add("11:30");
                    cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("12:30"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("13:30"); cam_Cheonan_terminal.add("14:00");
                    cam_Cheonan_terminal.add("14:30"); cam_Cheonan_terminal.add("14:50"); cam_Cheonan_terminal.add("15:10"); cam_Cheonan_terminal.add("15:30"); cam_Cheonan_terminal.add("15:40");
                    cam_Cheonan_terminal.add("16:00"); cam_Cheonan_terminal.add("16:30"); cam_Cheonan_terminal.add("16:40"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("17:20");
                    cam_Cheonan_terminal.add("17:30"); cam_Cheonan_terminal.add("17:45"); cam_Cheonan_terminal.add("18:10"); cam_Cheonan_terminal.add("18:30"); cam_Cheonan_terminal.add("18:50");
                    cam_Cheonan_terminal.add("19:10"); cam_Cheonan_terminal.add("19:40"); cam_Cheonan_terminal.add("20:10"); cam_Cheonan_terminal.add("20:50"); cam_Cheonan_terminal.add("21:30");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }
                    if(R.id.choice_c == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }
                }

                else if( (checkedId == R.id.choice_d))
                {
                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("08:40");asan_cam.add("08:50");asan_cam.add("08:55");asan_cam.add("09:05");asan_cam.add("09:15");
                    asan_cam.add("09:55");asan_cam.add("10:15");asan_cam.add("10:45");asan_cam.add("11:10");asan_cam.add("11:20");
                    asan_cam.add("11:50");asan_cam.add("12:05");asan_cam.add("12:20");asan_cam.add("12:45");asan_cam.add("13:15");
                    asan_cam.add("13:45");asan_cam.add("14:15");asan_cam.add("14:45");asan_cam.add("15:15");asan_cam.add("15:40");
                    asan_cam.add("15:50");asan_cam.add("16:15");asan_cam.add("16:40");asan_cam.add("16:55");asan_cam.add("17:20");
                    asan_cam.add("17:40");asan_cam.add("17:50");asan_cam.add("18:10");asan_cam.add("18:35");asan_cam.add("18:45");
                    asan_cam.add("19:45");asan_cam.add("20:05");asan_cam.add("20:25");asan_cam.add("20:45");asan_cam.add("21:05");
                    asan_cam.add("21:25");asan_cam.add("21:45");asan_cam.add("22:15");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("08:40");cam_asan.add("09:45");cam_asan.add("10:05");cam_asan.add("10:35");cam_asan.add("11:10");
                    cam_asan.add("11:40");cam_asan.add("11:55");cam_asan.add("12:10");cam_asan.add("12:35");cam_asan.add("13:05");
                    cam_asan.add("13:35");cam_asan.add("14:05");cam_asan.add("14:35");cam_asan.add("15:05");cam_asan.add("15:30");
                    cam_asan.add("15:40");cam_asan.add("16:05");cam_asan.add("16:30");cam_asan.add("16:45");cam_asan.add("17:10");
                    cam_asan.add("17:30");cam_asan.add("17:40");cam_asan.add("18:00");cam_asan.add("18:25");cam_asan.add("18:35");
                    cam_asan.add("19:00");cam_asan.add("19:20");cam_asan.add("19:40");cam_asan.add("20:00");cam_asan.add("20:20");
                    cam_asan.add("20:40");cam_asan.add("21:00");cam_asan.add("21:30");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:15");Cheonan_cam.add("08:35");Cheonan_cam.add("08:45");Cheonan_cam.add("08:50");Cheonan_cam.add("08:53");
                    Cheonan_cam.add("08:56");Cheonan_cam.add("08:58");Cheonan_cam.add("09:00");Cheonan_cam.add("09:03");Cheonan_cam.add("09:10");
                    Cheonan_cam.add("09:50");Cheonan_cam.add("10:00");Cheonan_cam.add("10:20");Cheonan_cam.add("10:40");Cheonan_cam.add("10:50");
                    Cheonan_cam.add("10:55");Cheonan_cam.add("11:00");Cheonan_cam.add("11:10");Cheonan_cam.add("11:40");Cheonan_cam.add("12:00");
                    Cheonan_cam.add("12:05");Cheonan_cam.add("12:30");Cheonan_cam.add("13:00");Cheonan_cam.add("13:30");Cheonan_cam.add("14:00");
                    Cheonan_cam.add("14:30");Cheonan_cam.add("15:00");Cheonan_cam.add("15:30");Cheonan_cam.add("15:40");Cheonan_cam.add("15:50");
                    Cheonan_cam.add("16:00");Cheonan_cam.add("16:20");Cheonan_cam.add("16:55");Cheonan_cam.add("17:05");Cheonan_cam.add("17:25");
                    Cheonan_cam.add("17:50");Cheonan_cam.add("18:00");Cheonan_cam.add("18:15");Cheonan_cam.add("18:40");Cheonan_cam.add("18:55");
                    Cheonan_cam.add("19:05");Cheonan_cam.add("19:30");Cheonan_cam.add("19:50");Cheonan_cam.add("20:10");Cheonan_cam.add("20:30");
                    Cheonan_cam.add("20:50");Cheonan_cam.add("21:10");Cheonan_cam.add("21:30");Cheonan_cam.add("22:20");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:15");cam_Cheonan.add("09:35");cam_Cheonan.add("09:55");cam_Cheonan.add("10:35");cam_Cheonan.add("11:15");
                    cam_Cheonan.add("11:40");cam_Cheonan.add("12:10");cam_Cheonan.add("12:40");cam_Cheonan.add("13:10");cam_Cheonan.add("13:40");
                    cam_Cheonan.add("14:10");cam_Cheonan.add("14:40");cam_Cheonan.add("15:10");cam_Cheonan.add("15:20");cam_Cheonan.add("15:30");
                    cam_Cheonan.add("15:40");cam_Cheonan.add("16:00");cam_Cheonan.add("16:30");cam_Cheonan.add("16:40");cam_Cheonan.add("17:00");
                    cam_Cheonan.add("17:20");cam_Cheonan.add("17:30");cam_Cheonan.add("17:45");cam_Cheonan.add("18:10");cam_Cheonan.add("18:25");
                    cam_Cheonan.add("18:35");cam_Cheonan.add("19:00");cam_Cheonan.add("19:20");cam_Cheonan.add("19:40");cam_Cheonan.add("20:00");
                    cam_Cheonan.add("20:20");cam_Cheonan.add("20:40");cam_Cheonan.add("21:00");cam_Cheonan.add("21:30");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:10");Cheonan_terminal_cam.add("08:25");Cheonan_terminal_cam.add("08:35");Cheonan_terminal_cam.add("08:40");Cheonan_terminal_cam.add("08:45");
                    Cheonan_terminal_cam.add("08:50");Cheonan_terminal_cam.add("08:55");Cheonan_terminal_cam.add("09:00");Cheonan_terminal_cam.add("09:40");Cheonan_terminal_cam.add("09:55");
                    Cheonan_terminal_cam.add("10:15");Cheonan_terminal_cam.add("10:35");Cheonan_terminal_cam.add("10:50");Cheonan_terminal_cam.add("11:00");Cheonan_terminal_cam.add("11:30");
                    Cheonan_terminal_cam.add("11:45");Cheonan_terminal_cam.add("12:00");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:00");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("14:00");Cheonan_terminal_cam.add("14:30");Cheonan_terminal_cam.add("15:00");Cheonan_terminal_cam.add("15:20");Cheonan_terminal_cam.add("15:40");
                    Cheonan_terminal_cam.add("16:00");Cheonan_terminal_cam.add("16:10");Cheonan_terminal_cam.add("16:30");Cheonan_terminal_cam.add("17:00");Cheonan_terminal_cam.add("17:10");
                    Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("17:50");Cheonan_terminal_cam.add("18:00");Cheonan_terminal_cam.add("18:15");Cheonan_terminal_cam.add("18:40");
                    Cheonan_terminal_cam.add("19:00");Cheonan_terminal_cam.add("19:20");Cheonan_terminal_cam.add("19:40");Cheonan_terminal_cam.add("20:10");Cheonan_terminal_cam.add("20:40");
                    Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("22:00");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("07:45"); cam_Cheonan_terminal.add("08:10"); cam_Cheonan_terminal.add("08:20"); cam_Cheonan_terminal.add("09:10"); cam_Cheonan_terminal.add("09:25");
                    cam_Cheonan_terminal.add("09:45"); cam_Cheonan_terminal.add("10:05"); cam_Cheonan_terminal.add("10:30"); cam_Cheonan_terminal.add("11:00"); cam_Cheonan_terminal.add("11:30");
                    cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("12:30"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("13:30"); cam_Cheonan_terminal.add("14:00");
                    cam_Cheonan_terminal.add("14:30"); cam_Cheonan_terminal.add("14:50"); cam_Cheonan_terminal.add("15:10"); cam_Cheonan_terminal.add("15:30"); cam_Cheonan_terminal.add("15:40");
                    cam_Cheonan_terminal.add("16:00"); cam_Cheonan_terminal.add("16:30"); cam_Cheonan_terminal.add("16:40"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("17:20");
                    cam_Cheonan_terminal.add("17:30"); cam_Cheonan_terminal.add("17:45"); cam_Cheonan_terminal.add("18:10"); cam_Cheonan_terminal.add("18:30"); cam_Cheonan_terminal.add("18:50");
                    cam_Cheonan_terminal.add("19:10"); cam_Cheonan_terminal.add("19:40"); cam_Cheonan_terminal.add("20:10"); cam_Cheonan_terminal.add("20:50"); cam_Cheonan_terminal.add("21:30");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }
                    if(R.id.choice_d == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }

                }

                else if(checkedId == R.id.choice_e)
                {
                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("08:40");asan_cam.add("08:50");asan_cam.add("08:55");asan_cam.add("09:05");asan_cam.add("09:15");
                    asan_cam.add("09:55");asan_cam.add("10:15");asan_cam.add("10:45");asan_cam.add("11:05");asan_cam.add("11:20");
                    asan_cam.add("11:50");asan_cam.add("12:20");asan_cam.add("12:45");asan_cam.add("13:15");
                    asan_cam.add("13:45");asan_cam.add("14:15");asan_cam.add("14:45");asan_cam.add("15:15");asan_cam.add("15:40");
                    asan_cam.add("16:15");asan_cam.add("16:40");asan_cam.add("17:20");
                    asan_cam.add("17:40");asan_cam.add("18:10");asan_cam.add("18:45");
                    asan_cam.add("19:45");asan_cam.add("20:05");asan_cam.add("20:25");asan_cam.add("20:45");asan_cam.add("21:05");
                    asan_cam.add("21:25");asan_cam.add("21:45");asan_cam.add("22:15");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("08:40");cam_asan.add("09:45");cam_asan.add("10:05");cam_asan.add("10:35");cam_asan.add("11:10");
                    cam_asan.add("11:40");cam_asan.add("12:10");cam_asan.add("12:35");cam_asan.add("13:05");cam_asan.add("13:35");
                    cam_asan.add("14:05");cam_asan.add("14:35");cam_asan.add("15:05");cam_asan.add("15:30");cam_asan.add("16:05");
                    cam_asan.add("16:30");asan_cam.add("17:10");cam_asan.add("17:30");cam_asan.add("18:00");cam_asan.add("18:35");
                    cam_asan.add("19:00");cam_asan.add("19:20");cam_asan.add("19:40");cam_asan.add("20:00");cam_asan.add("20:20");
                    cam_asan.add("20:40");cam_asan.add("21:00");cam_asan.add("21:30");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:15");Cheonan_cam.add("08:35");Cheonan_cam.add("08:45");Cheonan_cam.add("08:50");Cheonan_cam.add("09:00");
                    Cheonan_cam.add("09:10");Cheonan_cam.add("09:50");Cheonan_cam.add("10:00");Cheonan_cam.add("10:20");Cheonan_cam.add("10:40");
                    Cheonan_cam.add("11:00");Cheonan_cam.add("11:10");Cheonan_cam.add("11:40");Cheonan_cam.add("12:00");Cheonan_cam.add("12:05");
                    Cheonan_cam.add("12:30");Cheonan_cam.add("13:00");Cheonan_cam.add("13:30");Cheonan_cam.add("14:00");Cheonan_cam.add("14:30");
                    Cheonan_cam.add("15:00");Cheonan_cam.add("15:30");Cheonan_cam.add("15:50");Cheonan_cam.add("16:20");Cheonan_cam.add("16:55");
                    Cheonan_cam.add("17:25");Cheonan_cam.add("18:00");Cheonan_cam.add("18:40");Cheonan_cam.add("19:05");Cheonan_cam.add("19:30");
                    Cheonan_cam.add("19:50");Cheonan_cam.add("20:10");Cheonan_cam.add("20:30");Cheonan_cam.add("20:50");Cheonan_cam.add("21:10");
                    Cheonan_cam.add("21:30");Cheonan_cam.add("22:00");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:15");cam_Cheonan.add("09:35");cam_Cheonan.add("09:55");cam_Cheonan.add("10:35");cam_Cheonan.add("11:15");
                    cam_Cheonan.add("11:40");cam_Cheonan.add("12:10");cam_Cheonan.add("12:40");cam_Cheonan.add("13:10");cam_Cheonan.add("13:40");
                    cam_Cheonan.add("14:10");cam_Cheonan.add("14:40");cam_Cheonan.add("15:10");cam_Cheonan.add("15:30");cam_Cheonan.add("16:00");
                    cam_Cheonan.add("16:30");cam_Cheonan.add("17:00");cam_Cheonan.add("17:30");cam_Cheonan.add("18:10");cam_Cheonan.add("18:35");
                    cam_Cheonan.add("19:00");cam_Cheonan.add("19:20");cam_Cheonan.add("19:40");cam_Cheonan.add("20:00");cam_Cheonan.add("20:20");
                    cam_Cheonan.add("20:40");cam_Cheonan.add("21:00");cam_Cheonan.add("21:30");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:10");Cheonan_terminal_cam.add("08:25");Cheonan_terminal_cam.add("08:35");Cheonan_terminal_cam.add("08:50");Cheonan_terminal_cam.add("09:00");
                    Cheonan_terminal_cam.add("09:40");Cheonan_terminal_cam.add("09:55");Cheonan_terminal_cam.add("10:15");Cheonan_terminal_cam.add("10:35");Cheonan_terminal_cam.add("11:00");
                    Cheonan_terminal_cam.add("11:30");Cheonan_terminal_cam.add("12:00");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:00");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("14:00");Cheonan_terminal_cam.add("14:30");Cheonan_terminal_cam.add("15:00");Cheonan_terminal_cam.add("15:20");Cheonan_terminal_cam.add("15:40");
                    Cheonan_terminal_cam.add("16:00");Cheonan_terminal_cam.add("16:30");Cheonan_terminal_cam.add("17:00");Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("18:00");
                    Cheonan_terminal_cam.add("18:40");Cheonan_terminal_cam.add("19:00");Cheonan_terminal_cam.add("19:20");Cheonan_terminal_cam.add("19:40");Cheonan_terminal_cam.add("20:10");
                    Cheonan_terminal_cam.add("20:40");Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("22:00");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("07:45"); cam_Cheonan_terminal.add("08:10"); cam_Cheonan_terminal.add("08:20"); cam_Cheonan_terminal.add("09:10"); cam_Cheonan_terminal.add("09:25");
                    cam_Cheonan_terminal.add("09:45"); cam_Cheonan_terminal.add("10:05"); cam_Cheonan_terminal.add("10:30"); cam_Cheonan_terminal.add("11:00"); cam_Cheonan_terminal.add("11:30");
                    cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("12:30"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("13:30"); cam_Cheonan_terminal.add("14:00");
                    cam_Cheonan_terminal.add("14:30"); cam_Cheonan_terminal.add("14:50"); cam_Cheonan_terminal.add("15:10"); cam_Cheonan_terminal.add("15:30"); cam_Cheonan_terminal.add("16:00");
                    cam_Cheonan_terminal.add("16:30"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("17:30"); cam_Cheonan_terminal.add("18:10"); cam_Cheonan_terminal.add("18:30");
                    cam_Cheonan_terminal.add("18:50"); cam_Cheonan_terminal.add("19:10"); cam_Cheonan_terminal.add("19:40"); cam_Cheonan_terminal.add("20:10"); cam_Cheonan_terminal.add("20:50");
                    cam_Cheonan_terminal.add("21:30");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }

                    if(R.id.choice_e == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }

                }
                else if(checkedId == R.id.choice_f)
                {
                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("08:45");asan_cam.add("09:45");asan_cam.add("10:45");asan_cam.add("12:45");asan_cam.add("13:45");
                    asan_cam.add("15:45");asan_cam.add("17:45");asan_cam.add("18:45");asan_cam.add("19:45");asan_cam.add("20:45");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("08:00");cam_asan.add("09:00");cam_asan.add("10:00");cam_asan.add("12:00"); cam_asan.add("13:00");
                    cam_asan.add("15:00");cam_asan.add("17:00");cam_asan.add("18:00");cam_asan.add("19:00");cam_asan.add("20:00");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("08:30");Cheonan_cam.add("09:30");Cheonan_cam.add("10:30");Cheonan_cam.add("12:30");Cheonan_cam.add("13:30");
                    Cheonan_cam.add("15:30");Cheonan_cam.add("17:30");Cheonan_cam.add("18:30");Cheonan_cam.add("19:30");Cheonan_cam.add("20:30");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("08:00");cam_Cheonan.add("09:00");cam_Cheonan.add("10:00");cam_Cheonan.add("12:00");cam_Cheonan.add("13:00");
                    cam_Cheonan.add("15:00");cam_Cheonan.add("17:00");cam_Cheonan.add("18:00");cam_Cheonan.add("19:00");cam_Cheonan.add("20:00");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("08:30");Cheonan_terminal_cam.add("09:30");Cheonan_terminal_cam.add("10:30");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:30");
                    Cheonan_terminal_cam.add("15:30");Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("18:30");Cheonan_terminal_cam.add("19:30");Cheonan_terminal_cam.add("20:30");


                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("08:00"); cam_Cheonan_terminal.add("09:00"); cam_Cheonan_terminal.add("10:00"); cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("13:00");
                    cam_Cheonan_terminal.add("15:00"); cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("18:00"); cam_Cheonan_terminal.add("19:00"); cam_Cheonan_terminal.add("20:00");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }

                    if(R.id.choice_f == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }
                }
                else if(checkedId == R.id.choice_g)
                {

                    //array_reset();

                    asan_cam = new ArrayList<>();
                    asan_cam.add("09:45");asan_cam.add("10:35");asan_cam.add("12:45");asan_cam.add("13:45");asan_cam.add("15:45");
                    asan_cam.add("17:45");asan_cam.add("18:45");asan_cam.add("19:45");asan_cam.add("20:25");asan_cam.add("21:10");
                    asan_cam.add("21:35");asan_cam.add("21:45");

                    cam_asan = new ArrayList<>();
                    cam_asan.add("09:00");cam_asan.add("09:50");cam_asan.add("12:00");cam_asan.add("13:00");cam_asan.add("15:00");
                    cam_asan.add("17:00");cam_asan.add("18:00");cam_asan.add("19:00");cam_asan.add("19:40");cam_asan.add("20:25");
                    cam_asan.add("20:50");cam_asan.add("21:00");

                    Cheonan_cam = new ArrayList<>();
                    Cheonan_cam.add("09:30");Cheonan_cam.add("10:20");Cheonan_cam.add("12:30");Cheonan_cam.add("13:30");Cheonan_cam.add("15:30");
                    Cheonan_cam.add("17:30");Cheonan_cam.add("18:30");Cheonan_cam.add("19:30");Cheonan_cam.add("20:10");Cheonan_cam.add("20:55");
                    Cheonan_cam.add("20:20");Cheonan_cam.add("21:30");

                    cam_Cheonan = new ArrayList<>();
                    cam_Cheonan.add("09:00");cam_Cheonan.add("09:50");cam_Cheonan.add("12:00");cam_Cheonan.add("13:00");cam_Cheonan.add("15:00");
                    cam_Cheonan.add("17:00");cam_Cheonan.add("18:00");cam_Cheonan.add("19:00");cam_Cheonan.add("19:40");cam_Cheonan.add("20:25");
                    cam_Cheonan.add("20:50");cam_Cheonan.add("21:00");

                    Cheonan_terminal_cam = new ArrayList<>();
                    Cheonan_terminal_cam.add("09:30");Cheonan_terminal_cam.add("10:20");Cheonan_terminal_cam.add("12:30");Cheonan_terminal_cam.add("13:30");Cheonan_terminal_cam.add("15:30");
                    Cheonan_terminal_cam.add("17:30");Cheonan_terminal_cam.add("18:30");Cheonan_terminal_cam.add("19:30");Cheonan_terminal_cam.add("20:20");Cheonan_terminal_cam.add("20:50");
                    Cheonan_terminal_cam.add("21:20");Cheonan_terminal_cam.add("21:30");

                    cam_Cheonan_terminal = new ArrayList<>();
                    cam_Cheonan_terminal.add("09:00"); cam_Cheonan_terminal.add("09:50"); cam_Cheonan_terminal.add("12:00"); cam_Cheonan_terminal.add("13:00"); cam_Cheonan_terminal.add("15:00");
                    cam_Cheonan_terminal.add("17:00"); cam_Cheonan_terminal.add("18:00"); cam_Cheonan_terminal.add("19:00"); cam_Cheonan_terminal.add("19:50"); cam_Cheonan_terminal.add("20:20");
                    cam_Cheonan_terminal.add("20:50"); cam_Cheonan_terminal.add("21:00");

                    switch (singleSelectToggleGroup2.getCheckedId())
                    {
                        case R.id.choice_osan:
                            textView.setText(Arrays.toString(new ArrayList[]{asan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_asan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                        case R.id.choice_Cheonan_terminal:
                            textView.setText(Arrays.toString(new ArrayList[]{Cheonan_terminal_cam}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            textView2.setText(Arrays.toString(new ArrayList[]{cam_Cheonan_terminal}).replaceAll("\\[|\\]", " ").replace(",","     "),TextView.BufferType.SPANNABLE);
                            break;
                    }

                    if(R.id.choice_g == curr_Day) {
                        //System.out.println(dayday);

                        switch (singleSelectToggleGroup2.getCheckedId()) {
                            case R.id.choice_osan:
                                current_asan_cam();
                                current_cam_osan();
                                break;
                            case R.id.choice_Cheonan:
                                current_Cheonan_cam();
                                current_cam_Cheonan();
                                break;
                            case R.id.choice_Cheonan_terminal:
                                current_Cheonan_terminal_cam();
                                current_cam_Cheonan_terminal();
                                break;
                        }
                    }
                    else
                    {
                        scroll_set();
                    }




                }


            }
        });
    }

    @Override
    public void onResume() {

        System.out.println("온리줌");



        scrollView_start.post(new Runnable() {
            @Override
            public void run() {
               // scrollView_start.fling(0);
               // scrollView_start.smoothScrollTo(0,190);

                scrollView_start.scrollTo(0,0);

                scrollView_arrive.scrollTo(0,0);

                //index가 2보다 클경우
                ObjectAnimator.ofInt(scrollView_start, "scrollY", (10 - 2) *190).setDuration(250).start();

                ObjectAnimator.ofInt(scrollView_arrive, "scrollY", (6 - 2) *190).setDuration(250).start();

            }
        });

        super.onResume();

    }

    // onPause()일때 즉 사용자가 해당 Fragemnt를 떠났을때 호출이 됩니다
    @Override
    public void onPause() {

        System.out.println("온퓨즈");

        array_reset();

        super.onPause();
    }

    //현재 요일은 반환하는 함수
    private void current_day()
    {
        //현재 요일
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.KOREA);
       weekDay = weekdayFormat.format(currentTime);

    }

    //스크롤 셋
    private void scroll_set()
    {
        textView.post(new Runnable() {
            @Override public void run()
            {
                textView.scrollTo(0,0);
                textView.scrollBy(0,0);
            }
        });

        textView2.post(new Runnable() {
            @Override public void run()
            {
                textView2.scrollTo(0,0);
                textView2.scrollBy(0,0);
            }
        });
    }

    //현재 요일로 버튼 셋
    private void check_butt(String day)
    {

        singleSelectToggleGroup.clearCheck();

        if(day.equals("월"))
        {
            singleSelectToggleGroup.check(R.id.choice_a);
            curr_Day = R.id.choice_a;
        }
        else if(day.equals("화"))
        {
            singleSelectToggleGroup.check(R.id.choice_b);
            curr_Day = R.id.choice_b;
        }
        else if(day.equals("수"))
        {
            singleSelectToggleGroup.check(R.id.choice_c);
            curr_Day = R.id.choice_c;
        }
        else if(day.equals("목"))
        {
            singleSelectToggleGroup.check(R.id.choice_d);
            curr_Day = R.id.choice_d;
        }
        else if(day.equals("금"))
        {
            singleSelectToggleGroup.check(R.id.choice_e);
            curr_Day = R.id.choice_e;
        }
        else if(day.equals("토"))
        {
            singleSelectToggleGroup.check(R.id.choice_f);
            curr_Day = R.id.choice_f;
        }
        else if(day.equals("일"))
        {
            singleSelectToggleGroup.check(R.id.choice_g);
            curr_Day = R.id.choice_g;
        }

    }

    //현재 시간 아산 -> 캠
    public void current_asan_cam()
    {
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
            if (i > (asan_cam.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(asan_cam.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView.post(new Runnable() {
            @Override public void run()
            {
                textView.scrollTo(0,0);
                textView.scrollBy(x,0);
            }
        });
    }

    //현재 시간 캠 -> 아산
    public void current_cam_osan()
    {
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
            if (i > (cam_asan.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(cam_asan.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView2.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView2.post(new Runnable() {
            @Override public void run()
            {
                textView2.scrollTo(0,0);
                textView2.scrollBy(x,0);
            }
        });
    }


    //현재 시간 천안 -> 캠
    public void current_Cheonan_cam()
    {
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
            if (i > (Cheonan_cam.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(Cheonan_cam.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView.post(new Runnable() {
            @Override public void run()
            {
                textView.scrollTo(0,0);
                textView.scrollBy(x,0);
            }
        });
    }

    //현재 시간 캠 -> 천안
    public void current_cam_Cheonan()
    {
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
            if (i > (cam_Cheonan.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(cam_Cheonan.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView2.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView2.post(new Runnable() {
            @Override public void run()
            {
                textView2.scrollTo(0,0);
                textView2.scrollBy(x,0);
            }
        });
    }

    //현재 시간 천안 -> 캠
    public void current_Cheonan_terminal_cam()
    {
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
            if (i > (Cheonan_terminal_cam.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(Cheonan_terminal_cam.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView.post(new Runnable() {
            @Override public void run()
            {
                textView.scrollTo(0,0);
                textView.scrollBy(x,0);
            }
        });
    }

    //현재 시간 캠 -> 천안
    public void current_cam_Cheonan_terminal()
    {
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
            if (i > (cam_Cheonan_terminal.size() -1)) {
                i = 0;
                break;
            }
            try {
                endDate = dateSet.parse(cam_Cheonan_terminal.get(i));
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

        //색칠
        Spannable spannable = (Spannable)textView2.getText();
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#F85B4E")), (11*i)+2, (11*i)+7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //이동 하면됨

        final int x = i * 300;

        textView2.post(new Runnable() {
            @Override public void run()
            {
                textView2.scrollTo(0,0);
                textView2.scrollBy(x,0);
            }
        });
    }

}