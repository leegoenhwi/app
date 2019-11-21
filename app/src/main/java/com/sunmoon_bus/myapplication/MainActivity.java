package com.sunmoon_bus.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private   DrawerLayout dlDrawer;
    private Toolbar tb;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    int id; // 드로어 네비게이션 뷰 인덱스
    private int ig; // 바텀 네비게이션 뷰 인덱스
    private  long time; // 뒤로가기 카운트
    private Frag1 frag1;
    private Frag2 frag2;
    private Frag3 frag3;
    private Fragment fragment;
    private CustomDialog dialog; // 알림창
    private popup_dialog popup; // 공지사항
    private NavigationView navigationView;
    private BottomSheetDialog bottomSheetDialog;
    private Button infor_butt;
    //private BottomSheetDialog
    private AdView mAdView;
    private SharedPreferences sp;
    private String day;
    private final String PREF_FIRST_START = "AppFirstLaunch";
    private  SharedPreferences settings;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //세로로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main); // activiry_main 레이아웃과 연결


        tb = (Toolbar) findViewById(R.id.app_toolbar); // 툴바 변수명 설정
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        setSupportActionBar(tb); // 툴바 실행

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false); // 툴바 = 타이틀 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바 = <- 버튼 보이게 하기
        getSupportActionBar().setHomeButtonEnabled(true);

        popup = new popup_dialog(MainActivity.this,getLeftListener,getRightListener);// 공지사항

        popup.create();

        frag1 = new Frag1(); //프래그먼트 객채셍성
        frag2 = new Frag2(); //프래그먼트 객채셍성
        frag3 = new Frag3(); //프래그먼트 객채셍성
        fragment = new Fragment();

        dialog = new CustomDialog(MainActivity.this,leftListener); // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.infor_dialog);


        infor_butt = (Button)bottomSheetDialog.findViewById(R.id.infor_butt);

        infor_butt.setOnClickListener(MainActivity.this);

        day = get_today();

        System.out.println("날짜");
        System.out.println(day);

        //변수 생성 및 초기화
        sp = getSharedPreferences("fake_db", Activity.MODE_PRIVATE);

        home_icon();

        setFrag(0); //프래그먼트 초기화 함수
        initNavigationDrawer(); // 드로어 네비게이션 뷰 초기화 함수
        bottomNavigationView(); // 바텀 네비게이션 뷰 초기화 함수

        MobileAds.initialize(this,"ca-app-pub-5331304879189183~1559540203");

        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        popup.setCanceledOnTouchOutside(false);
        popup.setCancelable(true);
        popup_compare();

    }


    //다이얼로그
    public void Dialog()
    {
        dialog.setCanceledOnTouchOutside(false);	// 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        dialog.setCancelable(true); // 백키로 다이알로그 닫기
        dialog.show();
    }


    //홈 화면에 추가
    private void home_icon()
    {
       settings = getSharedPreferences(PREF_FIRST_START, 0);


        if(settings.getBoolean("AppFirstLaunch", true)){  // 아이콘이 두번 추가 안되도록 하기 위해서 필요한 체크입니다.


            settings.edit().putBoolean("AppFirstLaunch", false).commit();

            if (ShortcutManagerCompat.isRequestPinShortcutSupported(this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(this, "#1")
                        .setIntent(new Intent(this, MainActivity.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                        .setShortLabel(getString(R.string.app_name)) //  아이콘에 같이 보여질 이름
                        .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher_icon_round))  //아이콘에 보여질 이미지
                        .build();
                ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null);
            }
            else
            {
                // Shortcut is not supported by your launcher
            }

        }
    }

    //현재날짜 가져오기
    private String get_today() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");

        String format_time1 = format1.format(System.currentTimeMillis());

        return format_time1;
    }

    //오늘 그만보기 비교
    @SuppressLint("ApplySharedPref")
    public void popup_compare(){
            if(sp.getString("popup", "false").equals(day))
            {

            }
            else{

                popup.show();
            }
     }


    //다이얼로그 클릭이벤트
    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };


    //공지사항 클릭이벤트
    //확인
    private View.OnClickListener getLeftListener = new View.OnClickListener(){
        public void onClick(View v){
            popup.dismiss();
        }
    };
    //오늘하루 그만보기
    private View.OnClickListener getRightListener = new View.OnClickListener(){
        @SuppressLint("ApplySharedPref")
        public void onClick(View v){
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor edit = sp.edit();

            edit.putString("popup",day);

            edit.commit();
            popup.dismiss();
        }
    };





    //드로어 네비게이션 뷰
    public void initNavigationDrawer() {

        // 네비게이션 뷰 아이템 선택시 동작
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                id = menuItem.getItemId();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lily.sunmoon.ac.kr/Page/About/About08_04_02_01_01_01.aspx")); // 연결 URI 주소


                switch (id){

                    case R.id.gps:
                        bottomNavigationView.getMenu().findItem(R.id.bottombaritem_gps).setChecked(true);
                        fragment = frag1;
                        break;

                    case R.id.personnel:
                        bottomNavigationView.getMenu().findItem(R.id.bottombaritem_personnel).setChecked(true);
                        fragment = frag2;
                        break;

                    case R.id.quick_bus_time:
                        bottomNavigationView.getMenu().findItem(R.id.bottombaritem_quick_bus_time).setChecked(true);
                        fragment = frag3;
                        break;

                    case R.id.bus_time:

                        startActivity(intent);
                        break;

                    case R.id.notice:

                        break;

                    case R.id.n_t:

                        break;

                    case R.id.infor:

                        break;

                }

                dlDrawer.closeDrawer(GravityCompat.START,true);

               dlDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        switch (id) {
                            case R.id.bus_time:
                                break;
                            case R.id.n_t:
                                popup.show();
                                break;
                            case R.id.notice:
                                Dialog();
                                break;
                            case R.id.infor:
                                bottomSheetDialog.show();
                                break;
                            case R.id.gps:
                            case R.id.personnel:
                            case R.id.quick_bus_time: Frg_ani();
                                break;
                        }
                    }
                }, 300);


                return true;
            }

        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,dlDrawer,tb,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        dlDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    //드로어 네비게이션 뷰 (프래그먼트를 교체하는 함수)
    public void Frg_ani()
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    //버텀 네비게이션 뷰
    public void bottomNavigationView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        ig = item.getItemId();

                        switch (ig) {

                            case R.id.bottombaritem_gps:
                                navigationView.getMenu().findItem(R.id.gps).setChecked(true);
                                setFrag(0);
                                return true;

                            case R.id.bottombaritem_personnel:
                                navigationView.getMenu().findItem(R.id.personnel).setChecked(true);
                                setFrag(1);
                                return true;

                            case R.id.bottombaritem_quick_bus_time:
                                navigationView.getMenu().findItem(R.id.quick_bus_time).setChecked(true);
                                setFrag(2);
                                return true;
                        }
                        return false;
                    }
                });
    }

    //버텀 네비게이션 (프래그먼트를 교체하는 함수)
    public void setFrag(int n){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tran = fm.beginTransaction();


        switch (n){
            case 0:
                tran.replace(R.id.main_frame, frag1);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
            case 1:
                tran.replace(R.id.main_frame, frag2);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
            case 2:
                tran.replace(R.id.main_frame, frag3);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
        }
    }


    @Override // 뒤로가기 이벤트
    public void onBackPressed(){
        if(bottomSheetDialog.isShowing())
        {
            bottomSheetDialog.dismiss();
        }
        if(dlDrawer.isDrawerOpen(GravityCompat.START)){
            dlDrawer.closeDrawer(GravityCompat.START,true);
        }
        else if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
           finish();

        }
    }


    //클릭 이벤트
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.infor_butt: bottomSheetDialog.dismiss();
                break;
        }
    }

    @Override
    public void onDestroy()
    {
        System.exit(0);
        super.onDestroy();
    }
}

