package com.example.bokjirock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

public class detailInfo extends Activity {
    private String address1="";
    private String Nm="";
    private String Dgst="";
    private String ANm="";
    private String ALink="";
    private String Scrit="";
    private String Tgtr="";
    private String Juror="";
    private String JurMno="";
    private policyDetail pd;
    private Thread apiThread;
    private String query="";
    private TextView p_name;
    private TextView p_detail;
    private TextView p_howto;
    private TextView p_policyis;
    private TextView p_link;
    private TextView p_condition;
    private TextView p_condition_detail;
    private TextView p_location;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case 101:
                    modifyPD();
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
    public void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.policy_detail);
        address1= getIntent().getStringExtra("address");
        query="http://www.bokjiro.go.kr/openapi/rest/gvmtWelSvc?crtiKey=rdw30zhS7kTarAscsrFuTMFxGC4RKeLM69MkiAIKH9nQaTXRYtU%2FQqG3ZHQqLS4iaPvMUBPte4%2FMSApoW6j6eQ%3D%3D&callTp=D&servId="+address1;
        viewInit();
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
                    while (eventType != XmlPullParser.END_DOCUMENT) {   //가져온 내용이 끝이 아닐때까지 수행
                        String tag = xpars.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG://태그가 시작됨
                                tag = xpars.getName();//태그 이름 얻어오기
                                if (tag.equals("wantedDtl")) {// 서브리스트 태그가 한 서비스마다 시작 알려주는 거
                                    pd = new policyDetail();
                                }
                                break;
                            case XmlPullParser.TEXT:     //텍스받으면 각각에 내용에 맞게 함수 실행시켜 넣어줌
                                text = xpars.getText();
                                break;
                            case XmlPullParser.END_TAG:       //</servList>이런 식으 태그 만나면 끝이므로 실행
                                if (tag.equals("alwServCn")) {    //텍스트에 들어온 값 넣어줌
                                    pd.setAlwServCn(text);
                                } else if (tag.equals("servSeDetailLink")) {
                                    pd.setAppleservSeDetailLink(text);
                                }
                                else if(tag.equals("servSeDetailNm")) {
                                    pd.setAppleservSeDetailNm(text);
                                } else if (tag.equals("jurMnofNm")) {
                                    pd.setJurMnofNm(text);
                                } else if (tag.equals("jurOrgNm")) {
                                    pd.setJurOrgNm(text);
                                    Log.e("아아1234",text);
                                }else if (tag.equals("servDgst")) {
                                    pd.setServDgst(text);
                                } else if (tag.equals("servId")) {
                                    pd.setServId(text);
                                } else if (tag.equals("servNm")) {
                                    pd.setServNm(text);
                                } else if (tag.equals("slctCritCn")) {
                                    pd.setSlctCritCn(text);
                                } else if (tag.equals("tgtrDtlCn")) {
                                    pd.setTgtrDtlCn(text);
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

    public void modifyPD(){
        Nm=pd.getServNm().replaceAll("[<>br/]","");
        Dgst=pd.getServDgst().replaceAll("[<>br/]","");
        //ALink=pd.getAppleservSeDetailLink().replaceAll("[<>br/]","");
        ANm=pd.getAppleservSeDetailNm().replaceAll("[<>br/]","");
        Scrit=pd.getSlctCritCn().replaceAll("[<>br/]","");
        Tgtr=pd.getTgtrDtlCn().replaceAll("[<>br/]","");
        Juror=pd.getJurOrgNm().replaceAll("[<>br/]","");
        JurMno=pd.getJurMnofNm().replaceAll("[<>br/]","");
        setview();
    }

    public void setview(){
        p_name.setText(Nm);
        p_detail.setText("(1) 정책 세부 요약 사항:\n"+ Dgst+"\n");
        p_howto.setText("(2) 서비스 이용 및 신청방법:\n"+ANm+"\n");
        p_policyis.setText("(3) 신청 링크:");
        p_link.setText(Html.fromHtml(pd.getAppleservSeDetailLink()));
        p_condition.setText("\n(4) 선정기준:\n"+Scrit+"\n");
        p_condition_detail.setText("(5) 대상자 상세내용:\n"+Tgtr+"\n");
        p_location.setText("(6) 소관부처:\n"+Juror+" "+ JurMno+"\n");
    }

    public void viewInit(){
        p_name=findViewById(R.id.policyName);
        p_detail=findViewById(R.id.policyDetail);
        p_howto=findViewById(R.id.policyHowto);
        p_link=findViewById(R.id.policyLink);
        p_condition=findViewById(R.id.policyCondition);
        p_condition_detail=findViewById(R.id.policyCondition_detail);
        p_location=findViewById(R.id.policyLocation);
        p_policyis=findViewById(R.id.policyis);
        p_link.setClickable(true);
        p_link.setMovementMethod(LinkMovementMethod.getInstance());;
        ImageButton share_button=findViewById(R.id.share_button);


        share_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, "복지 서비스 이름: " +Nm+"\n\n");
                msg.putExtra(Intent.EXTRA_TEXT, "(1)세부 요약 사항:" + Dgst +"\n\n"+ "(2)신청방법: " + ANm+"\n\n" + "더 알고 싶다면 복지樂 을 검색해봐!");
                msg.putExtra(Intent.EXTRA_TITLE, "제목");
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "공유"));

            }
        });
    }
}

