package com.learnknots.wesslnelson.Sorter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        final Button startGame = (Button) findViewById(R.id.start);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // requesting to turn the title OFF
                //requestWindowFeature(Window.FEATURE_NO_TITLE);
                // making it full screen
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // set our MainGamePanel as the View
                //setContentView(new MainGamePanel(MainActivity.this));
                Intent intent = new Intent(MainActivity.this, StartGame.class);
                startActivity(intent);
                Log.d(TAG, "Game started");
            }
        });
        Log.d(TAG, "Home screen launched");


    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopping...");
        super.onStop();
    }
}