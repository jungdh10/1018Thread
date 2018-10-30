package com.example.a503_14.a1018thread;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.beans.IndexedPropertyChangeEvent;

class AsyncTaskEx extends AsyncTask<Integer, Integer, Integer>{

    //MainActivity의 데이터를 저장하기 위한 변수
    ProgressBar progressBar;
    TextView textView;
    int value;
    //생성자-MainActivity에서 데이터를 넘겨받기 위해 생성
    //MainActivity에 생성하면 클래스가 다르기 때문에 progressBar 등 사용하면 에러 그래서
    //사용할 다른 클래스안에 변수와 생성자 생성, 또는 클래스안의 클래스(내부클래스)로 생성해도 됨
    public AsyncTaskEx(ProgressBar progressBar, TextView textView, int value){
        this.progressBar=progressBar;
        this.textView=textView;
        this.value=value;
    }

    @Override
    //인스턴스가 생성되면 가장 먼저 호출되는 메소드
    //메인스레드에서 수행: 화면을 갱신하는 코드를 작성해도 됨
    protected void onPreExecute() {
        super.onPreExecute();
        //UI 초기화
        value=0;
        progressBar.setProgress(value);
    }

    @Override
    //비동기적인 작업을 처리하는 메소드, 매개변수는클래스를 생성할 때 적용한 제너릭의 두번째 자료형과 일치해야 함
    //리턴타입은 세번째 자료형과 일치해야 함
    //메인스레드에서 동작하지 않음
    //UI 갱신하는 코드는 작성할 수 없음
    protected Integer doInBackground(Integer... integers) {
        while(isCancelled()==false){
            value=value+1;
            if(value>=100){
                break;
            }else{
                //onProgressUpdate 호출
                publishProgress(value);
            }
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                Log.e(" 문제 발생",e.getMessage());
            }
        }
        return value;
    }

    @Override
    //doInBackground에서 PublishProgress를 호출하면 자동으로 호출되는 메소드
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(value);
        textView.setText(String.format("현재값: %d", value));
    }

    @Override
    //인스턴스가 cancelled를 호출했을 때 호출되는 메소드
    //메인스레드에서 실행
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
        value=0;
        progressBar.setProgress(value);
        textView.setText("스레드 중지");
    }

    @Override
    //doInBackground가 작업을 종료했을 때 호출되는 메소드
    //메인스레드에서 실행
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        //종료될경우 다시 0부터 시작하고 싶을 때
        //value=0;
        //progressBar.setProgress(value);
        textView.setText("스레드 종료");
    }

}


public class MainActivity extends AppCompatActivity {
    //프로그래스바와 텍스트뷰 변수를 선언
    ProgressBar progressBar;
    TextView textView;
    //프로그래스바의 값을 표시할 정수 변수 선언
    int value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start=(Button)findViewById(R.id.start);
        Button stop=(Button)findViewById(R.id.end);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        textView=(TextView)findViewById(R.id.textView);

        View.OnClickListener clickListener=new View.OnClickListener(){
            AsyncTaskEx task=null;
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.start:
                        task=new AsyncTaskEx(progressBar, textView, value);
                        task.execute(100);
                        break;
                    case R.id.end:
                        task.cancel(true);
                        break;
                }
            }
        };
        start.setOnClickListener(clickListener);
        stop.setOnClickListener(clickListener);
    }
}
