package com.sunmoon_bus.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Frag1 extends Fragment implements OnMapReadyCallback{

    private View view;
    private MapView mapView = null;
    private String url_gps = "http://119.67.32.123:8840/VSS.php";

    private String[] gps_split;
    private String[] s_gps;
    private List<LatLng> set_marker;

    private GoogleMap mygoogleMap;
    private boolean stopFlag = false;

    private ArrayList<Marker> bus_markers;

    private JAT task ;

    private Marker ch_marker = null,osa_marker = null,ter_marker = null ,sunmoon_marker = null;

    private ArrayList<LatLng> sourcepoints,sourcePoints2,sourcePoints,sourcePoints3, sourcePoints4,sourcePoints5,sourcePoints6;

    private int gps_count = 1;

    private int bus_count = 0;

    private Marker bus_marker_token = null;

    public Frag1() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
        System.out.println("온크리에이트");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        System.out.println("온크리에이트뷰");
        view = inflater.inflate(R.layout.fragment1, container, false);

        mapView = (MapView)view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        bus_markers = new ArrayList<Marker>();


        set_marker = new ArrayList<LatLng>();
        s_gps = new String[]{"0", "0", "0"};

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onResume() {
        mapView.onResume();

        //AsyncTask 생성, 실행
        task = new JAT();
        stopFlag = false;


        task.execute();

        System.out.println("온리줌");

        super.onResume();

    }

    // onPause()일때 즉 사용자가 해당 Fragemnt를 떠났을때 호출이 됩니다
    @Override
    public void onPause()
    {
        mapView.onPause();
        stopFlag = true;
        bus_markers.clear();
        bus_marker_token = null;
        task.cancel(true);
        System.out.println("온퓨즈");



        super.onPause();
    }


    @Override
    public void onStop()
    {
        mapView.onStop();
        System.out.println("온스탐");
        super.onStop();


    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mygoogleMap.clear();
        //System.out.println("온디스트로이");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        //System.out.println("온로우메모리");
        super.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("온맵레디");


        mygoogleMap = googleMap;

        MapsInitializer.initialize(this.getActivity());


        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(36.810185, 127.112129), 12);

        //카메라 위치 설정
        googleMap.moveCamera(cameraUpdate);

        //고정 마커 초기화
        initi_fix_marker(googleMap);

        //경로 그리기
        poly_line(googleMap);



    }


    //아이콘 사이즈 조절
    private Bitmap getMarkerBitmapFromView_marker() {

        int height = 60;
        int width = 60;
        BitmapDrawable bitmapdraw=(BitmapDrawable)view.getResources().getDrawable(R.drawable.map_marker,null);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;

    }

    //셔틀버스 아이콘 사이즈 조절
    private Bitmap getMarkerBitmapFromView_bus() {

        int height = 40;
        int width = 40;
        BitmapDrawable bitmapdraw=(BitmapDrawable)view.getResources().getDrawable(R.drawable.map_bus,null);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;

    }

    //실시간 버스 위치 갱신 초기화
    private void in_gps_bus()
    {


       if(bus_marker_token != null)
        {
            System.out.println("버스 마커 업데이트");


            for(int i = 0;i<bus_count;i++)
            {
                bus_markers.get(i).setPosition(set_marker.get(i));
            }


        }

        if(bus_marker_token == null) {
            System.out.println("버스 마커 생김");

            for(int i = 0;i<bus_count;i++)
            {

                bus_marker_token = mygoogleMap.addMarker(new MarkerOptions()
                        .position(set_marker.get(i))
                        .title("셔틀 버스")
                        .anchor(0.5f,0.5f));

                bus_marker_token.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_bus()));
                bus_marker_token.setZIndex(10);

                bus_markers.add(bus_marker_token);

            }


        }


    }




    //경로 그리기
    private void poly_line(GoogleMap googleMap)
    {

        //List<LatLng> sourcepoints,sourcePoints2,sourcePoints,sourcePoints3, sourcePoints4,sourcePoints5

        //학교 라인
         sourcepoints = new ArrayList<>();
        sourcepoints.add(new LatLng(36.799402, 127.071820));
        sourcepoints.add(new LatLng(36.799431, 127.073535));
        sourcepoints.add(new LatLng(36.799463, 127.076354));
        sourcepoints.add(new LatLng(36.799497, 127.077941));
        sourcepoints.add(new LatLng(36.798045, 127.077987));

        //불당 대로
       sourcePoints2 = new ArrayList<>();
        sourcePoints2.add(new LatLng(36.800301, 127.099290));
        sourcePoints2.add(new LatLng(36.800353, 127.099577));
        sourcePoints2.add(new LatLng(36.800331, 127.100007));
        sourcePoints2.add(new LatLng(36.799173, 127.100269));
        sourcePoints2.add(new LatLng(36.798900, 127.100331));
        sourcePoints2.add(new LatLng(36.798462, 127.100449));
        sourcePoints2.add(new LatLng(36.798029, 127.100398));

        //아산역 -> 학교 중심 경로
       sourcePoints = new ArrayList<>();
        sourcePoints.add(new LatLng(36.800397, 127.071243));
        sourcePoints.add(new LatLng(36.800247, 127.070935));
        sourcePoints.add(new LatLng(36.800214, 127.070812));
        sourcePoints.add(new LatLng(36.800116, 127.070868));
        sourcePoints.add(new LatLng(36.799954, 127.071066));
        sourcePoints.add(new LatLng(36.799783, 127.071378));
        sourcePoints.add(new LatLng(36.799402, 127.071820));
        sourcePoints.add(new LatLng(36.797981, 127.071813));
        sourcePoints.add(new LatLng(36.797981, 127.073578));
        sourcePoints.add(new LatLng(36.797971, 127.073574));
        sourcePoints.add(new LatLng(36.798045, 127.077987));
        sourcePoints.add(new LatLng(36.797675, 127.078749));
        sourcePoints.add(new LatLng(36.797624, 127.079003));
        sourcePoints.add(new LatLng(36.797663, 127.079490));
        sourcePoints.add(new LatLng(36.797746, 127.079869));
        sourcePoints.add(new LatLng(36.797750, 127.080308));
        sourcePoints.add(new LatLng(36.797593, 127.081461));
        sourcePoints.add(new LatLng(36.797538, 127.081825));
        sourcePoints.add(new LatLng(36.797508, 127.084763));
        sourcePoints.add(new LatLng(36.797499, 127.085803));
        sourcePoints.add(new LatLng(36.797520, 127.086802));
        sourcePoints.add(new LatLng(36.797522, 127.086938));
        sourcePoints.add(new LatLng(36.797515, 127.087022));
        sourcePoints.add(new LatLng(36.797493, 127.087114));
        sourcePoints.add(new LatLng(36.797456, 127.087204));
        sourcePoints.add(new LatLng(36.797378, 127.087299));
        sourcePoints.add(new LatLng(36.797310, 127.087349));
        sourcePoints.add(new LatLng(36.797259, 127.087372));
        sourcePoints.add(new LatLng(36.796120, 127.087596));
        sourcePoints.add(new LatLng(36.795764, 127.087710));
        sourcePoints.add(new LatLng(36.795865, 127.087888));
        sourcePoints.add(new LatLng(36.798755, 127.093553));
        sourcePoints.add(new LatLng(36.799378, 127.095124));
        sourcePoints.add(new LatLng(36.800061, 127.097727));
        sourcePoints.add(new LatLng(36.800301, 127.099290));
        sourcePoints.add(new LatLng(36.800250, 127.099420));
        sourcePoints.add(new LatLng(36.800200, 127.099541));
        sourcePoints.add(new LatLng(36.800166, 127.099603));
        sourcePoints.add(new LatLng(36.800139, 127.099633));
        sourcePoints.add(new LatLng(36.800106, 127.099645));
        sourcePoints.add(new LatLng(36.799072, 127.099943));
        sourcePoints.add(new LatLng(36.798789, 127.100023));
        sourcePoints.add(new LatLng(36.798533, 127.100099));
        sourcePoints.add(new LatLng(36.798284, 127.100191));
        sourcePoints.add(new LatLng(36.798029, 127.100398));
        sourcePoints.add(new LatLng(36.796218, 127.100862));
        //왼쪽 위
        sourcePoints.add(new LatLng(36.795284, 127.101086));
        sourcePoints.add(new LatLng(36.795384, 127.101697));
        sourcePoints.add(new LatLng(36.795515, 127.102681));
        sourcePoints.add(new LatLng(36.795494, 127.102881));
        sourcePoints.add(new LatLng(36.795447, 127.103001));
        sourcePoints.add(new LatLng(36.795337, 127.103084));
        sourcePoints.add(new LatLng(36.795220, 127.103150));
        sourcePoints.add(new LatLng(36.794443, 127.103361));
        //아산역 셔틀장
        sourcePoints.add(new LatLng(36.794443, 127.103441));

        //학교 -> 아산역
       sourcePoints3 = new ArrayList<>();
        sourcePoints3.add(new LatLng(36.795284, 127.101086));
        sourcePoints3.add(new LatLng(36.792610, 127.101762));
        sourcePoints3.add(new LatLng(36.792085, 127.101940));
        sourcePoints3.add(new LatLng(36.792201, 127.102015));
        sourcePoints3.add(new LatLng(36.792206, 127.102023));
        sourcePoints3.add(new LatLng(36.792349, 127.102136));
        sourcePoints3.add(new LatLng(36.792395, 127.102200));
        sourcePoints3.add(new LatLng(36.792437, 127.102332));
        sourcePoints3.add(new LatLng(36.792501, 127.102491));
        sourcePoints3.add(new LatLng(36.792681, 127.103393));
        sourcePoints3.add(new LatLng(36.792726, 127.103521));
        sourcePoints3.add(new LatLng(36.792778, 127.103606));
        sourcePoints3.add(new LatLng(36.792863, 127.103688));
        sourcePoints3.add(new LatLng(36.792959, 127.103727));
        sourcePoints3.add(new LatLng(36.793081, 127.103732));
        sourcePoints3.add(new LatLng(36.794443, 127.103361));

        //천안역 -> 학교 중심경로
       sourcePoints4 = new ArrayList<>();
        sourcePoints4.add(new LatLng(36.810071, 127.143373));
        sourcePoints4.add(new LatLng(36.810031, 127.143361));
        sourcePoints4.add(new LatLng(36.809991, 127.143332));
        sourcePoints4.add(new LatLng(36.809930, 127.143380));
        sourcePoints4.add(new LatLng(36.809858, 127.143431));
        sourcePoints4.add(new LatLng(36.809815, 127.143451));
        sourcePoints4.add(new LatLng(36.809781, 127.143463));
        sourcePoints4.add(new LatLng(36.809760, 127.143470));
        sourcePoints4.add(new LatLng(36.809704, 127.143482));
        sourcePoints4.add(new LatLng(36.809673, 127.143488));
        sourcePoints4.add(new LatLng(36.809640, 127.143493));
        sourcePoints4.add(new LatLng(36.809623, 127.143493));
        sourcePoints4.add(new LatLng(36.809593, 127.143451));
        sourcePoints4.add(new LatLng(36.809520, 127.143389));
        sourcePoints4.add(new LatLng(36.809363, 127.143262));
        sourcePoints4.add(new LatLng(36.809123, 127.143154));
        sourcePoints4.add(new LatLng(36.808947, 127.143091));
        sourcePoints4.add(new LatLng(36.808145, 127.143114));
        sourcePoints4.add(new LatLng(36.806630, 127.143160));
        sourcePoints4.add(new LatLng(36.806209, 127.143140));
        sourcePoints4.add(new LatLng(36.805937, 127.143165));
        sourcePoints4.add(new LatLng(36.804841, 127.143330));
        sourcePoints4.add(new LatLng(36.804650, 127.143338));
        sourcePoints4.add(new LatLng(36.804506, 127.143415));
        sourcePoints4.add(new LatLng(36.804482, 127.143426));
        sourcePoints4.add(new LatLng(36.804432, 127.143432));
        sourcePoints4.add(new LatLng(36.803083, 127.143396));
        sourcePoints4.add(new LatLng(36.802953, 127.143446));
        sourcePoints4.add(new LatLng(36.802758, 127.142679));
        sourcePoints4.add(new LatLng(36.802531, 127.140936));
        sourcePoints4.add(new LatLng(36.802468, 127.140398));
        sourcePoints4.add(new LatLng(36.802328, 127.139544));
        sourcePoints4.add(new LatLng(36.802082, 127.138739));
        sourcePoints4.add(new LatLng(36.801703, 127.137574));
        sourcePoints4.add(new LatLng(36.801475, 127.137126));
        sourcePoints4.add(new LatLng(36.801293, 127.136795));
        sourcePoints4.add(new LatLng(36.801064, 127.136336));
        sourcePoints4.add(new LatLng(36.800756, 127.135746));
        sourcePoints4.add(new LatLng(36.800581, 127.135375));
        sourcePoints4.add(new LatLng(36.800291, 127.134653));
        sourcePoints4.add(new LatLng(36.799795, 127.133100));
        sourcePoints4.add(new LatLng(36.799282, 127.131667));
        sourcePoints4.add(new LatLng(36.799214, 127.131509));
        sourcePoints4.add(new LatLng(36.799127, 127.131306));
        sourcePoints4.add(new LatLng(36.799097, 127.131214));
        sourcePoints4.add(new LatLng(36.799068, 127.131152));
        sourcePoints4.add(new LatLng(36.798776, 127.130976));
        sourcePoints4.add(new LatLng(36.798339, 127.130705));
        sourcePoints4.add(new LatLng(36.798246, 127.130645));
        sourcePoints4.add(new LatLng(36.798215, 127.130559));
        sourcePoints4.add(new LatLng(36.798168, 127.130440));
        sourcePoints4.add(new LatLng(36.798170, 127.130296));
        sourcePoints4.add(new LatLng(36.798188, 127.130210));
        sourcePoints4.add(new LatLng(36.798207, 127.130153));
        sourcePoints4.add(new LatLng(36.798267, 127.129995));
        sourcePoints4.add(new LatLng(36.798688, 127.129377));
        sourcePoints4.add(new LatLng(36.798868, 127.129107));
        sourcePoints4.add(new LatLng(36.799295, 127.128291));
        sourcePoints4.add(new LatLng(36.799346, 127.128174));
        sourcePoints4.add(new LatLng(36.799511, 127.127811));
        sourcePoints4.add(new LatLng(36.799606, 127.127612));
        sourcePoints4.add(new LatLng(36.799696, 127.127228));
        sourcePoints4.add(new LatLng(36.799760, 127.126897));
        sourcePoints4.add(new LatLng(36.799797, 127.126717));
        sourcePoints4.add(new LatLng(36.799833, 127.126551));
        sourcePoints4.add(new LatLng(36.799873, 127.126383));
        sourcePoints4.add(new LatLng(36.799912, 127.126193));
        sourcePoints4.add(new LatLng(36.800036, 127.125470));
        //-----------쌍용 아파트-----------//
        sourcePoints4.add(new LatLng(36.800164, 127.124690));
        sourcePoints4.add(new LatLng(36.800383, 127.123431));
        sourcePoints4.add(new LatLng(36.800697, 127.121629));
        sourcePoints4.add(new LatLng(36.800836, 127.120625));
        sourcePoints4.add(new LatLng(36.801109, 127.118517));
        sourcePoints4.add(new LatLng(36.801504, 127.115335));
        sourcePoints4.add(new LatLng(36.801600, 127.114512));
        sourcePoints4.add(new LatLng(36.801836, 127.112498));
        sourcePoints4.add(new LatLng(36.801860, 127.112314));
        sourcePoints4.add(new LatLng(36.801891, 127.112122));
        sourcePoints4.add(new LatLng(36.801908, 127.111907));
        sourcePoints4.add(new LatLng(36.801926, 127.111705));
        sourcePoints4.add(new LatLng(36.801978, 127.110615));
        sourcePoints4.add(new LatLng(36.802001, 127.109564));
        sourcePoints4.add(new LatLng(36.801977, 127.108945));
        sourcePoints4.add(new LatLng(36.801960, 127.108657));
        sourcePoints4.add(new LatLng(36.801838, 127.107486));
        sourcePoints4.add(new LatLng(36.801791, 127.107158));
        sourcePoints4.add(new LatLng(36.801654, 127.106313));
        sourcePoints4.add(new LatLng(36.801575, 127.105857));
        sourcePoints4.add(new LatLng(36.801530, 127.105628));
        sourcePoints4.add(new LatLng(36.801476, 127.105370));
        sourcePoints4.add(new LatLng(36.801425, 127.105102));
        sourcePoints4.add(new LatLng(36.800461, 127.100269));
        sourcePoints4.add(new LatLng(36.800437, 127.100088));
        sourcePoints4.add(new LatLng(36.800353, 127.099577));

        //천안 터미널 -> 학교 중심경로
        sourcePoints5 = new ArrayList<>();
        sourcePoints5.add(new LatLng(36.818780, 127.153430));
        sourcePoints5.add(new LatLng(36.818707, 127.153438));
        sourcePoints5.add(new LatLng(36.818979, 127.158221));
        sourcePoints5.add(new LatLng(36.819046, 127.158557));
        sourcePoints5.add(new LatLng(36.819243, 127.159023));
        sourcePoints5.add(new LatLng(36.821070, 127.162062));
        sourcePoints5.add(new LatLng(36.821569, 127.162185));
        sourcePoints5.add(new LatLng(36.823123, 127.160931));
        sourcePoints5.add(new LatLng(36.823742, 127.160584));
        sourcePoints5.add(new LatLng(36.824078, 127.160347));
        sourcePoints5.add(new LatLng(36.824276, 127.160128));
        sourcePoints5.add(new LatLng(36.824499, 127.159777));
        sourcePoints5.add(new LatLng(36.824647, 127.159471));
        sourcePoints5.add(new LatLng(36.824760, 127.159153));
        sourcePoints5.add(new LatLng(36.824807, 127.158791));
        sourcePoints5.add(new LatLng(36.824785, 127.157740));
        sourcePoints5.add(new LatLng(36.824718, 127.156267));
        sourcePoints5.add(new LatLng(36.824691, 127.153079));
        sourcePoints5.add(new LatLng(36.824682, 127.152391));
        sourcePoints5.add(new LatLng(36.824933, 127.149048));
        sourcePoints5.add(new LatLng(36.824963, 127.148615)); //
        sourcePoints5.add(new LatLng(36.825279, 127.144554));
        sourcePoints5.add(new LatLng(36.825578, 127.140914));
        sourcePoints5.add(new LatLng(36.825620, 127.140526));
        sourcePoints5.add(new LatLng(36.825627, 127.140246));
        sourcePoints5.add(new LatLng(36.825852, 127.137344));
        sourcePoints5.add(new LatLng(36.826348, 127.134120));
        sourcePoints5.add(new LatLng(36.826503, 127.133158));
        sourcePoints5.add(new LatLng(36.826509, 127.132996));
        sourcePoints5.add(new LatLng(36.826493, 127.132838));
        sourcePoints5.add(new LatLng(36.826379, 127.132539));
        sourcePoints5.add(new LatLng(36.824748, 127.127320));
        sourcePoints5.add(new LatLng(36.824177, 127.125633));
        sourcePoints5.add(new LatLng(36.823820, 127.124387));
        sourcePoints5.add(new LatLng(36.823646, 127.123955));
        sourcePoints5.add(new LatLng(36.823600, 127.123721));
        sourcePoints5.add(new LatLng(36.823538, 127.123363));
        sourcePoints5.add(new LatLng(36.822888, 127.118249));
        sourcePoints5.add(new LatLng(36.822729, 127.116649));
        sourcePoints5.add(new LatLng(36.822662, 127.115204));
        sourcePoints5.add(new LatLng(36.822673, 127.114442));
        sourcePoints5.add(new LatLng(36.822704, 127.113617));
        sourcePoints5.add(new LatLng(36.822791, 127.112411));
        sourcePoints5.add(new LatLng(36.822687, 127.112194));
        sourcePoints5.add(new LatLng(36.822605, 127.112032));
        sourcePoints5.add(new LatLng(36.822520, 127.111906));
        sourcePoints5.add(new LatLng(36.822337, 127.111730));
        sourcePoints5.add(new LatLng(36.821629, 127.111502));
        sourcePoints5.add(new LatLng(36.804736, 127.106464));
        sourcePoints5.add(new LatLng(36.804261, 127.106355));
        sourcePoints5.add(new LatLng(36.804197, 127.106250));
        sourcePoints5.add(new LatLng(36.802856, 127.105612));
        sourcePoints5.add(new LatLng(36.801878, 127.105349));
        sourcePoints5.add(new LatLng(36.801700, 127.105332));
        sourcePoints5.add(new LatLng(36.801572, 127.105304));
        sourcePoints5.add(new LatLng(36.801425, 127.105102));

        //터미널 주변
        sourcePoints6 = new ArrayList<>();
        sourcePoints6.add(new LatLng(36.818707, 127.153438));
        sourcePoints6.add(new LatLng(36.818645, 127.152150));
        sourcePoints6.add(new LatLng(36.818634, 127.151526));
        sourcePoints6.add(new LatLng(36.818628, 127.151442));
        sourcePoints6.add(new LatLng(36.818626, 127.150888));
        sourcePoints6.add(new LatLng(36.818618, 127.150503));
        sourcePoints6.add(new LatLng(36.818610, 127.147891));
        sourcePoints6.add(new LatLng(36.818612, 127.147816));
        sourcePoints6.add(new LatLng(36.818615, 127.147719));
        sourcePoints6.add(new LatLng(36.818688, 127.146141));
        sourcePoints6.add(new LatLng(36.818724, 127.145338));
        sourcePoints6.add(new LatLng(36.818826, 127.145269));
        sourcePoints6.add(new LatLng(36.818982, 127.145161));
        sourcePoints6.add(new LatLng(36.819672, 127.144751));
        sourcePoints6.add(new LatLng(36.819716, 127.144729));
        sourcePoints6.add(new LatLng(36.819893, 127.144649));
        sourcePoints6.add(new LatLng(36.819944, 127.144625));
        sourcePoints6.add(new LatLng(36.820163, 127.144557));
        sourcePoints6.add(new LatLng(36.820509, 127.144523));
        sourcePoints6.add(new LatLng(36.821823, 127.144625));
        sourcePoints6.add(new LatLng(36.822088, 127.144737));
        sourcePoints6.add(new LatLng(36.822482, 127.144929));
        sourcePoints6.add(new LatLng(36.822895, 127.145194));
        sourcePoints6.add(new LatLng(36.823052, 127.145373));
        sourcePoints6.add(new LatLng(36.823196, 127.145529));
        sourcePoints6.add(new LatLng(36.823406, 127.145778));
        sourcePoints6.add(new LatLng(36.823970, 127.146603));
        sourcePoints6.add(new LatLng(36.824650, 127.148090));
        sourcePoints6.add(new LatLng(36.824696, 127.148182));
        sourcePoints6.add(new LatLng(36.824872, 127.148531));
        sourcePoints6.add(new LatLng(36.824896, 127.148564));
        sourcePoints6.add(new LatLng(36.824919, 127.148590));
        sourcePoints6.add(new LatLng(36.824963, 127.148615));


        poly_line_option(sourcepoints);
        poly_line_option(sourcePoints);
        poly_line_option(sourcePoints2);
        poly_line_option(sourcePoints3);
        poly_line_option(sourcePoints4);
        poly_line_option(sourcePoints5);
        poly_line_option(sourcePoints6);


    }

    //경로 옵션
    private void poly_line_option(List<LatLng> poly_line)
    {
        PolylineOptions polyLineOptions = new PolylineOptions();
        polyLineOptions.addAll(poly_line);
        polyLineOptions.width(9);
        polyLineOptions.color(Color.rgb(242,92,5));
        mygoogleMap.addPolyline(polyLineOptions);
    }


        //고정 마커 초기화 함수
    private void initi_fix_marker(GoogleMap googleMap)
    {

        //마커 생성
        sunmoon_marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(36.800397, 127.071243))
                .title("아산 캠퍼스 셔틀장")
                .anchor(0.5f,0.5f));

        ch_marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(36.810071, 127.143373))
                .title("천안역 셔틀장")
                .anchor(0.5f,0.5f));

        osa_marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(36.794443, 127.103441))
                .title("아산역 셔틀장")
                .anchor(0.5f,0.5f));

        ter_marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(36.818780, 127.153430))
                .title("천안 터미널 셔틀장")
                .anchor(0.5f,0.5f));


        //마커 객체 아이콘 설정
        sunmoon_marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_marker()));
        ch_marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_marker()));
        osa_marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_marker()));
        ter_marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_marker()));

        //z인덱스
        sunmoon_marker.setZIndex(0);
        ch_marker.setZIndex(0);
        osa_marker.setZIndex(0);
        ter_marker.setZIndex(0);

    }



    //gps 크롤링
    private class JAT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("온프리익스큐트");

        }


        @Override
        protected Void doInBackground(Void... params) {

            Document doc;


            while (!stopFlag) {
                System.out.println("온두인백그라운드");

                int [] numberArray = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

                bus_count = 0;
                gps_count = 1;

                if(!set_marker.isEmpty()) {
                    set_marker.clear();
                }


                try {

                    doc = Jsoup.connect(url_gps).get();

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

                System.out.println("온인백그라운드 try catch 넘어감");

                Elements gps = doc.select("div");
                System.out.println(gps.text());
                gps_split = gps.text().split(" ");

                for(int i=0;i<gps_split.length/5;i++) {
                    for (int j = 0; j < 3; j++) {
                        s_gps[j] = gps_split[i * 5 + j];
                        System.out.println("좌표");
                        System.out.println(s_gps[j]);
                    }
                     if(numberArray[gps_count] == 0 && Integer.parseInt(s_gps[0]) == gps_count) {
                         set_marker.add(new LatLng(Double.parseDouble(s_gps[1]), Double.parseDouble(s_gps[2])));

                         gps_count++;
                         bus_count++;
                     }

                }


                try {
                    System.out.println("버스 카운터");
                    System.out.println(bus_count);
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


            in_gps_bus();


        }

        @Override
        protected void onPostExecute(Void result) {
            System.out.println("어싱크테스크 종료");
        }


        // cancel 호출하면 실행 됨
        @Override
        protected void onCancelled() {
            super.onCancelled();
            System.out.println("온 캔슬");
        }


    }



}
