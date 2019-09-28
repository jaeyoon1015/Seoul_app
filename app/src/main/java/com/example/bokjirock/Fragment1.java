package com.example.bokjirock;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.acl.LastOwnerException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Fragment1 extends Fragment {

    private String queryUrl = "http://www.bokjiro.go.kr/openapi/rest/gvmtWelSvc?crtiKey=rdw30zhS7kTarAscsrFuTMFxGC4RKeLM69MkiAIKH9nQaTXRYtU%2FQqG3ZHQqLS4iaPvMUBPte4%2FMSApoW6j6eQ%3D%3D&callTp=L&pageNum=1&numOfRows=50";
    private Thread apiThread;
    AssetManager assetManager;
    private policyInput policyInput;
    private RecyclerView.LayoutManager LayoutManager1;
    private RecyclerView recyclerView1;
    private ArrayList<policyInput> policyInputArrayList;
    private ArrayList<policyInfo> policyInfoArrayList;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case 101:
                    makePolicyInfo(policyInputArrayList);
                    recyclerView1.setAdapter(new myAdapter(getContext(),policyInfoArrayList));
                    break;
                case 102:
                    //오류
                    Toast.makeText(getContext(), (String) message.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    public static Fragment1 newInstance() {
        Bundle args=new Bundle();
        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyInfoArrayList=new ArrayList<policyInfo>();
        policyInputArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment1, container, false);
        LayoutManager1 = new LinearLayoutManager(view.getContext());
        recyclerView1 = view.findViewById(R.id.recycle_view1);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(LayoutManager1);
        getsubwayApi();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        if (apiThread != null) {
            if (apiThread.isAlive()) {
                apiThread.interrupt();
            }
            apiThread = null;
        }

        super.onStop();
    }

    private void getsubwayApi() {
        Log.e("아아","11");
        policyInputArrayList.clear();
        apiThread = initgetAPiThread();
        apiThread.start();
    }

    private Thread initgetAPiThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                InputStream ins = null;
                try {
                    ins = new URL(queryUrl).openStream();      //二쇱냼 ?좎븣??諛쏆븘 ?ㅽ뵂?ㅽ듃由?

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpars = factory.newPullParser();
                    try {
                        xpars.setInput(new InputStreamReader(ins, "UTF-8"));
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                    int eventType = xpars.getEventType();
                    policyInfo policyInfo = null;
                    String text = "";
                    while (eventType != XmlPullParser.END_DOCUMENT) {   //가져온 내용이 끝이 아닐때까지 수행
                        String tag = xpars.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG://태그가 시작됨
                                tag = xpars.getName();//태그 이름 얻어오기
                                if (tag.equals("servList")) {// 서브리스트 태그가 한 서비스마다 시작 알려주는 거
                                    policyInput = new policyInput();
                                }
                                break;

                            case XmlPullParser.TEXT:     //텍스받으면 각각에 내용에 맞게 함수 실행시켜 넣어줌
                                text = xpars.getText();
                                break;

                            case XmlPullParser.END_TAG:       //</servList>이런 식으 태그 만나면 끝이므로 실행
                                if (tag.equals("servList")) {       //서브리스트라는 태그를 만나면 생성한 배열 원소 객체 하나 더해주기
                                    policyInputArrayList.add(policyInput);
                                } else if (tag.equals("inqNum")) {
                                    text=text.replaceAll("[,]","");
                                    policyInput.setInqNum(Integer.parseInt(text));
                                } else if (tag.equals("jurMnofNm")) {
                                    policyInput.setJurMnofNm(text);
                                } else if (tag.equals("jurOrgNm")) {
                                    policyInput.setJurOrgNm(text);
                                } else if (tag.equals("servDgst")) {
                                    policyInput.setServDgst(text);
                                } else if (tag.equals("servDtlLink")) {
                                    policyInput.setServDtlLink(text);
                                } else if (tag.equals("servId")) {
                                    policyInput.setServId(text);
                                } else if (tag.equals("servNm")) {
                                    policyInput.setServNm(text);
                                    Log.e("ㅎㅎ", text);
                                } else if (tag.equals("svcfrstRegTs")) {
                                    policyInput.setSvcfrstRegTs(text);
                                }
                                break;
                            default:
                                break;
                        }
                        eventType = xpars.next();    //다음 이벤트 타입으로 넘겨줌
                    }
                    ins.close();
                    msg.what = 101;
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    msg.what = 102;
                    msg.obj = "API 오류";
                }
                mHandler.sendMessage(msg);
            }
        });
    }

    private void makePolicyInfo(ArrayList<policyInput> policyInputArrayList){
        Collections.sort(policyInputArrayList, new Comparator<policyInput>() {
            @Override
            public int compare(com.example.bokjirock.policyInput p1, com.example.bokjirock.policyInput p2) {
                return p1.getSvcfrstRegTs().compareTo(p2.getSvcfrstRegTs());
            }
        });
        for(int i=0;i<policyInputArrayList.size();i++) {
            String ServDgst = policyInputArrayList.get(i).getServDgst().replaceAll("[<>br/]", "");
            policyInfoArrayList.add(new policyInfo(policyInputArrayList.get(i).getServNm(), ServDgst, "0", "0", policyInputArrayList.get(i).getServId()));
        }
    }
}