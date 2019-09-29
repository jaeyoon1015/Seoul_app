package com.example.bokjirock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class resultActivity extends Activity {
    private RecyclerView recyclerView1;
    private ArrayList<policyInput> policyInputArrayList;
    private ArrayList<policyInfo> policyInfoArrayList;
    private RecyclerView.LayoutManager LayoutManager1;
    private Thread apiThread;
    private String query;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case 101:
                    //정상
                    makePolicyInfo(policyInputArrayList);
                    recyclerView1.setAdapter(new myAdapter(getApplicationContext(), policyInfoArrayList));
                    break;
                case 102:
                    //오류
                    Toast.makeText(getApplicationContext(), "인터넷 연결이 불안정합니다. API오류" , Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        setContentView(R.layout.activity_result);
        recyclerView1 = findViewById(R.id.recycle_view_search);
        init();
        getsubwayApi();
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
        policyInputArrayList.clear();
        apiThread = initgetAPiThread();
        apiThread.start();
    }

    private void init() {
        policyInfoArrayList = new ArrayList<>();
        policyInputArrayList = new ArrayList<>();
        recyclerView1.setHasFixedSize(true);
        LayoutManager1 = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(LayoutManager1);
    }


    private Thread initgetAPiThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                InputStream ins = null;
                try {
                    ins = new URL(query).openStream();      //二쇱냼 ?좎븣??諛쏆븘 ?ㅽ뵂?ㅽ듃由?

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpars = factory.newPullParser();

                    try {
                        xpars.setInput(new InputStreamReader(ins, "UTF-8"));    //?명뭼由щ뜑濡??뚯떛???댁슜 ?ｊ린
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    int eventType = xpars.getEventType();
                    policyInput policyInput = null;
                    String text = "";
                    while (eventType != XmlPullParser.END_DOCUMENT) {   //媛?몄삩 ?댁슜???앹씠 ?꾨땺?뚭퉴吏 ?섑뻾
                        String tag = xpars.getName();

                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                tag = xpars.getName();//?쒓렇 ?대쫫 ?살뼱?ㅺ린
                                if (tag.equals("servList")) {// ?쒕툕由ъ뒪???쒓렇媛 ???쒕퉬?ㅻ쭏???쒖옉 ?뚮젮二쇰뒗 嫄?
                                    policyInput = new policyInput();
                                }
                                break;

                            case XmlPullParser.TEXT:     //?띿뒪諛쏆쑝硫?媛곴컖???댁슜??留욊쾶 ?⑥닔 ?ㅽ뻾?쒖폒 ?ｌ뼱以?
                                text = xpars.getText();
                                break;

                            case XmlPullParser.END_TAG:       //</servList>?대윴 ?앹쑝 ?쒓렇 留뚮굹硫??앹씠誘濡??ㅽ뻾
                                if (tag.equals("servList")) {       //서브리스트라는 태그를 만나면 생성한 배열 원소 객체 하나 더해주기
                                    policyInputArrayList.add(policyInput);
                                } else if (tag.equals("inqNum")) {
                                    text = text.replaceAll("[,]", "");
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
                        eventType = xpars.next();
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
        for(int i=0;i<policyInputArrayList.size();i++) {
            String ServDgst = policyInputArrayList.get(i).getServDgst().replaceAll("[<>br/]", "");
            //Log.e("여기11",ServDgst);
            policyInfoArrayList.add(new policyInfo(policyInputArrayList.get(i).getServNm(), ServDgst, "0", "0", policyInputArrayList.get(i).getServId()));
        }
    }
}
