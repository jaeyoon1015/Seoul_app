package com.example.bokjirock;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static android.R.layout.simple_spinner_dropdown_item;

public class searchActivity extends Fragment {
    private Button btnFinish;
    private Spinner spinner1, spinner2, spinner3, spinner4, spinnerAge;
    private String key = "rdw30zhS7kTarAscsrFuTMFxGC4RKeLM69MkiAIKH9nQaTXRYtU%2FQqG3ZHQqLS4iaPvMUBPte4%2FMSApoW6j6eQ%3D%3D";
    private String str = null;
    private String sAge="";
    private String sTarget="";
    private String sTypeObs="";
    private String sLevelObs="";
    private String sTypeHome="";
    ArrayAdapter<String> arrayAdapter;

    CheckBox typeALL, type00, type01, type02, type03, type04, type05,type06,type07, type08, type09, type0A;

    @Nullable
    public static searchActivity newInstance() {
        Bundle args=new Bundle();
        searchActivity fragment = new searchActivity();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Resources resources=getResources();
        View view = inflater.inflate(R.layout.activity_search, container, false);

        btnFinish = view.findViewById(R.id.btnFinish);  // 검색버튼
        spinnerAge = view.findViewById(R.id.spinnerAge);  // 나이
        spinner1 = view.findViewById(R.id.spinner1);      // 대상특성 여성, 임산부, 장애, ..
        spinner2 = view.findViewById(R.id.spinner2);      // 장애 클릭시 지체...시각, 청각..등등
        spinner3 = view.findViewById(R.id.spinner3);      // 장애 클릭시 몇급인지
        spinner4 = view.findViewById(R.id.spinner4);      // 가구 유형 한부모, 다문화, 조손 등등

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sAge= "00"+(position);
                if(position==0){
                    sAge="ALL";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                sAge="ALL";
            }
        });


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sTarget= "00"+(position+1);
                if(sTarget.equals("001")){
                    sTarget="ALL";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sTarget="ALL";
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sTypeObs= (position)+"0";
                if(sTypeObs.equals("00")){
                    sTypeObs="NULL";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sTypeObs="NULL";
            }
        });


        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sLevelObs= String.valueOf((position+1));
                if(position==0){
                    sLevelObs="NULL";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sLevelObs="NULL";
            }
        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sTypeHome= "00"+(position+1);
                if(sTypeHome.equals("001")){
                    sTypeHome="ALL";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sTypeHome="ALL";
            }
        });

        typeALL = view.findViewById(R.id.typeALL);
        type00 = view.findViewById(R.id.type00);
        type01 = view.findViewById(R.id.type01);
        type02 = view.findViewById(R.id.type02);
        type03 = view.findViewById(R.id.type03);
        type04 = view.findViewById(R.id.type04);
        type05 = view.findViewById(R.id.type05);
        type06 = view.findViewById(R.id.type06);
        type07 = view.findViewById(R.id.type07);
        type08 = view.findViewById(R.id.type08);
        type09 = view.findViewById(R.id.type09);
        type0A = view.findViewById(R.id.type0A);


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String option1, option2, option3, option4, option5;

                if (sAge.equals("ALL")) {
                    option1 = "";
                } else {
                    option1 = "&lifeArray=" + sAge;
                }
                if (sTarget.equals("ALL")) {
                    option2 = "";
                } else {
                    option2 = "&charTrgterArray=" + sTarget;
                }
                if(sTypeObs.equals("NULL")) {
                    option3 = "";
                } else {
                    option3 = "&obstKiArray=" + sTypeObs;
                }
                if (sLevelObs.equals("NULL")) {
                    option4 = "";
                } else {
                    option4 = "&obstLvArray=" + sLevelObs;
                }
                if (sTypeHome.equals("ALL")) {
                    option5 = "";
                } else {
                    option5 = "&trgterIndvdlArray=" + sTypeHome;
                }

                String typeUrl="";

                if(typeALL.isChecked()){
                    typeUrl.concat("");
                }else if (type00.isChecked()){
                    typeUrl.concat("&desireArray=0000000");
                }else if (type01.isChecked()){
                    typeUrl.concat("&desireArray=1000000");
                }else if (type02.isChecked()){
                    typeUrl.concat("&desireArray=2000000");
                }else if (type03.isChecked()){
                    typeUrl.concat("&desireArray=3000000");
                }else if (type04.isChecked()){
                    typeUrl.concat("&desireArray=4000000");
                }else if (type05.isChecked()){
                    typeUrl.concat("&desireArray=5000000");
                }else if (type06.isChecked()){
                    typeUrl.concat("&desireArray=6000000");
                }else if (type07.isChecked()){
                    typeUrl.concat("&desireArray=7000000");
                }else if (type08.isChecked()){
                    typeUrl.concat("&desireArray=8000000");
                }else if (type09.isChecked()){
                    typeUrl.concat("&desireArray=9000000");
                }else if (type0A.isChecked()){
                    typeUrl.concat("&desireArray=A000000");
                }else{ }

                str = "http://www.bokjiro.go.kr/openapi/rest/gvmtWelSvc?crtiKey=" + key + "&callTp=L&pageNum=1&numOfRows=100";
//                "http://www.bokjiro.go.kr/openapi/rest/gvmtWelSvc"//요청 URL
//               +"&callTp=L&pageNum=1&numOfRows=10&desireArray=0000000"+
//                "&ServiceKey="+key;

                String query = str + option1 + option2 + option3 + option4 + option5+ typeUrl;

                Intent intent = new Intent(getActivity().getApplicationContext(), resultActivity.class);
                intent.putExtra("query", query);
                Log.e("query",query);
                startActivity(intent);

                getActivity().finish();

            }
        });

        return view;
    }

}