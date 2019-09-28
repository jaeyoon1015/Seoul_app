package com.example.bokjirock;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import android.content.Context;

import com.example.bokjirock.LocationSearch.OnFinishListener;
import com.example.bokjirock.LocationSearch.PlaceItem;
import com.example.bokjirock.LocationSearch.Searcher;

import java.util.HashMap;
import java.util.List;

public class locationActivity extends Fragment implements
        MapView.MapViewEventListener,
        MapView.POIItemEventListener {
    private static final String LOG_TAG = "MainActivity";
    View view;
    private MapView mapView;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    private HashMap<Integer, PlaceItem> mTagItemMap = new HashMap<Integer, PlaceItem>();

    private Button nursinghomeSearchBtn;
    private Button search_btn2;
    private TextView placeurlTextView;
    private TextView phoneTextView;

    LinearLayout detailLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_location, container, false);

        nursinghomeSearchBtn = view.findViewById(R.id.search_nursinghome);
        search_btn2 = view.findViewById(R.id.search_btn2);
        mapView = view.findViewById(R.id.map_view);
        detailLayout = view.findViewById(R.id.place_detail);
        placeurlTextView = view.findViewById(R.id.place_url);
        phoneTextView = view.findViewById(R.id.phone);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        //위치 허용 체크
        if (!checkLocationServicesStatus())
            showDialogForLocationServiceSetting();
        else
            checkRunTimePermission();


        nursinghomeSearchBtn.setOnClickListener(searchbtnClickListener);
        search_btn2.setOnClickListener(searchbtnClickListener);
        placeurlTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setData(Uri.parse(placeurlTextView.getText().toString()));
                startActivity(intent);
            }
        });
        phoneTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:"+phoneTextView.getText().toString())));
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
        mapView.removeAllPOIItems();
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                //위치 값을 가져올 수 있음
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
//                    finish();

                }else {

                    Toast.makeText(getContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /*
    키워드 검색
     */
    OnFinishListener onFinishListener = new OnFinishListener() {
        @Override
        public void onSuccess(List<PlaceItem> itemList) {
            mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
            showResult(itemList); // 검색 결과 보여줌
        }
        @Override
        public void onFail(){
            Log.w("오류: ","오류");
        }
    };
    Button.OnClickListener searchbtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(detailLayout.getVisibility()==View.VISIBLE)
                detailLayout.setVisibility(View.GONE);

            MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
            final double latitude = geoCoordinate.latitude; // 위도
            final double longitude = geoCoordinate.longitude; // 경도
            final int radius = 1000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
            final int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
            Searcher searcher = new Searcher();
            final String apikey = searcher.key_kakao;
            String keyword;
            switch (view.getId()) {
                case R.id.search_nursinghome:
                    keyword = "양로원";
                    searcher.searchKeyword(getActivity().getApplicationContext(), keyword, latitude, longitude, radius, page, apikey, onFinishListener);
                    break;
                case R.id.search_btn2:
                    keyword = "보육원";
                    searcher.searchKeyword(getActivity().getApplicationContext(), keyword, latitude, longitude, radius, page, apikey, onFinishListener);
                    break;
            }
        }

    };

    //마커찍기
    private void showResult(List<PlaceItem> placeitemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        if(mTagItemMap.size() != 0)
            mTagItemMap.clear();
        for (int i = 0; i < placeitemList.size(); i++) {

            PlaceItem item = placeitemList.get(i);
            MapPOIItem marker = new MapPOIItem();

            marker.setTag(i);
            marker.setItemName(item.place_name);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.y, item.x);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            mapView.addPOIItem(marker); //마커 찍기
            mTagItemMap.put(marker.getTag(), item);

        }
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

    }

    //MapView.POIItemEventListener 오버라이딩 메소드
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
         detailLayout.setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.place_name)).setText(mTagItemMap.get(mapPOIItem.getTag()).place_name);
        ((TextView)view.findViewById(R.id.address_road)).setText(mTagItemMap.get(mapPOIItem.getTag()).address_name);
        ((TextView)view.findViewById(R.id.phone)).setText(mTagItemMap.get(mapPOIItem.getTag()).phone);
        ((TextView)view.findViewById(R.id.place_url)).setText(mTagItemMap.get(mapPOIItem.getTag()).place_url);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    //MapViewEventListener
    @Override
    public void onMapViewInitialized(MapView mapView) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        detailLayout.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }



}
