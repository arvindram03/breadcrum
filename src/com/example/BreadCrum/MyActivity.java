package com.example.BreadCrum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MyActivity extends Activity {

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intent = new Intent(this, CallListeningService.class);
        startService(intent);

    }


    public void toggleChange(View view){
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
        if(toggleButton.getText().equals("Inside Geofence")){
            stopService(intent);
        }else{
            startService(intent);
        }
        Toast.makeText(MyActivity.this,toggleButton.getText(), Toast.LENGTH_SHORT).show();
    }


}
