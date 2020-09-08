package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    CountDownTimer countDownTimer;
    boolean counting;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        counting = false;
        editText = findViewById(R.id.editTextTime);
        textView = findViewById(R.id.timerTextView);
        button = findViewById(R.id.button);
    }

    public void startClicked(View view){
        if(!counting) {
            button.setText("Stop");
            counting = true;
            String[] time = editText.getText().toString().split(":");
            textView.setText(time[0] + ":" + time[1]);
            int seconds = Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
            countDownTimer = new CountDownTimer(seconds*1000 + 100, 1000) {
                @Override
                public void onTick(long l) {
                    textView.setText(convert((int) l / 1000));
                }

                @Override
                public void onFinish() {
                    MediaPlayer.create(getApplicationContext(), R.raw.alarm).start();
                    reset();
                }
            }.start();
        }else{
            button.setText("Start");
            counting = false;
        }
    }
    public void reset(){
        countDownTimer.cancel();
        textView.setText("00:00");
        button.setText("Start");
    }
    public String convert(int sec){
        int min = sec/60;
        sec -= min*60;
        String s = sec+"";
        String m = min+"";
        if(sec<10)
            s = "0"+s;
        if(min<10)
            m = "0"+m;
        return m+":"+s;
    }
}