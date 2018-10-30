package com.example.a503_14.a1018thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

//스레드안에 핸들러 이용해보기 - Looper이용
public class LooperActivity extends AppCompatActivity {
    ArrayList<String> oddDatas;
    ArrayList<String> evenDatas;

    ArrayAdapter<String> oddAdapter;
    ArrayAdapter<String> evenAdapter;

    OneThread oneThread;
    TwoThread twoThread;

    //핸들러내용을 여기 줄수 있음(이전까지 해온)
    Handler handler;

    class OneThread extends Thread{
        //스레드 내부에 핸들러 만들기
        Handler oneHandler;

        public void run(){
            //스레드 내부에서 핸들러를 만들 때는 Looper 이용
            Looper.prepare();
            oneHandler = new Handler(){
                @Override
                public void handleMessage(Message message){
                    //1초 대기
                    SystemClock.sleep(1000);
                    //메시지의 전송 내용 가져오기
                    final int data = message.arg1;
                    //메시지를 구분
                    if(message.what == 0){
                        //다른 작업이 없을 때 처리
                        handler.post(new Runnable(){
                            @Override
                            public void run(){
                                //데이터를 추가하고 리스트 뷰 다시 출력
                                evenDatas.add("even:" + data);
                                evenAdapter.notifyDataSetChanged();
                            }
                        });
                    }else{
                        //다른 작업이 없을 때 처리
                        handler.post(new Runnable(){
                            @Override
                            public void run(){
                                //데이터를 추가하고 리스트 뷰 다시 출력
                                oddDatas.add("odd:" + data);
                                oddAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            };
            //루퍼를 반복
            Looper.loop();
        }
    }

    //랜덤한 정수를 생성해서  oneThread에게 메시지를 전송하는
    //스레드 클래스
    class TwoThread extends Thread{
        public void run(){
            Random r = new Random();
            for(int i=0; i<10; i=i+1){
                SystemClock.sleep(100);
                int data = r.nextInt(10);
                Message message = new Message();
                if(data % 2 == 0){
                    message.what=0;
                }else{
                    message.what=1;
                }
                message.arg1 = data;
                oneThread.oneHandler.sendMessage(message);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        oddDatas = new ArrayList<>();
        evenDatas = new ArrayList<>();

        oddAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, oddDatas);
        evenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, evenDatas);

        ListView oddListView = (ListView)findViewById(R.id.oddListView);
        ListView evenListView = (ListView)findViewById(R.id.evenListView);

        oddListView.setAdapter(oddAdapter);
        evenListView.setAdapter(evenAdapter);

        handler = new Handler();

        oneThread = new OneThread();
        oneThread.start();

        twoThread = new TwoThread();
        twoThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //생성한 루퍼 종료
        oneThread.oneHandler.getLooper().quit();
    }
}