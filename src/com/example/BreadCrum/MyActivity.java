package com.example.BreadCrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyActivity extends Activity {

    private Intent intent;
    private CountDownTimer countDownTimer;
    private Messenger messenger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intent = new Intent(this, CallListeningService.class);
        startService(intent);
        messenger = new Messenger(MyActivity.this);
        countDownTimer =  new CountDownTimer(500000, 15000) {

            public void onTick(long millisUntilFinished) {
                Toast.makeText(MyActivity.this,"seconds remaining: " + millisUntilFinished / 1000,Toast.LENGTH_SHORT).show();
                messenger.sendMessage("+919840794389");
            }

            public void onFinish() {
                Toast.makeText(MyActivity.this,"finished",Toast.LENGTH_SHORT).show();
            }
        };
    }


    public void toggleChange(View view){
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
        if(toggleButton.getText().equals("Inside Geofence")){
            stopService(intent);
            countDownTimer.cancel();
        }else{
            startService(intent);
            countDownTimer.start();
        }
        Toast.makeText(MyActivity.this,toggleButton.getText(), Toast.LENGTH_SHORT).show();
    }




}
