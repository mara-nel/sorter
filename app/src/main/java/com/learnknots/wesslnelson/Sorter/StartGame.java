package com.learnknots.wesslnelson.Sorter;

/**
 * Created by wesslnelson on 5/23/16.
 */

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class StartGame extends Activity {

    private static final String TAG = StartGame.class.getSimpleName();
    private MainGamePanel gamePanel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // set our MainGamePanel as the View
        gamePanel = new MainGamePanel(StartGame.this);
        setContentView(gamePanel);
        Log.d(TAG, "View added");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Pausing...");
        super.onPause();
        gamePanel.endIt();
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