package com.learnknots.wesslnelson.Sorter;

/**
 * Created by wesslnelson on 5/18/16.
 *
 * following tutorial by javacodegeeks.com
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.learnknots.wesslnelson.Sorter.model.Sortee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    Resources res = getResources();

    private static final    String TAG = MainGamePanel.class.getSimpleName();
    private final int       SAFE_ZONE_WIDTH = res.getInteger(R.integer.safeZone);
    private final int       NEW_SORTEE_TIME = res.getInteger(R.integer.timeBetweenRespawn);

    private long newSorteeTicker = 0;
    private String safeZoneTest;
    private String numberOfSortees;
    private int nextRespawn;

    private MainThread thread;
    private List<Sortee> sortees;


    public MainGamePanel(Context context) {
        super(context);
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // create droid and load bitmap
        //sortee = new Sortee(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 50, 50);
        //sortee2 =  new Sortee(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 100, 100);

        sortees = new ArrayList<Sortee>();
        numberOfSortees = Integer.toString(sortees.size());


        // create the main game loop thread
        thread = new MainThread(getHolder(), this);

        // make the GamePanel focusable so it can handle events
        setFocusable(true);

        safeZoneTest = "no one is in it";
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        boolean retry = true;
        while (retry) {
            try{
                thread.join();
                retry = false;
            } catch (InterruptedException e){
                // try again shutting down the thread®
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // delegating event handling to the droid
            //sortee.handleActionDown((int)event.getX(), (int)event.getY());
            for (Sortee sortee:sortees) {
                sortee.handleActionDown((int)event.getX(), (int)event.getY());
            }

            // check if in lower part of screen to see if exit
            if (event.getY() > getHeight() - 50) {
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            } else {
                Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // the gestures
            for (Sortee sortee:sortees) {
                if (sortee.isTouched()) {
                    // the droid was picked up and is being dragged
                    sortee.setX((int) event.getX());
                    sortee.setY((int) event.getY());
                }
            }
        } if (event.getAction() == MotionEvent.ACTION_UP) {
            // touch was released
            for (Sortee sortee:sortees) {
                if (sortee.isTouched()) {
                    sortee.setTouched(false);
                }
            }
        }
        return true;
    }


    protected void render(Canvas canvas) {
        // fills the canvas with black
        canvas.drawColor(Color.CYAN);
        drawSafeLine(canvas, this.getWidth() - SAFE_ZONE_WIDTH);

        //sortee.draw(canvas);
        //sortee2.draw(canvas);

        // draw all sortees in list
        for( Sortee sortee: sortees) {
            sortee.draw(canvas);
        }
        displayText(canvas, safeZoneTest, 20);
        displayText(canvas, numberOfSortees, 40);

    }

    private void drawSafeLine(Canvas canvas, int safeZone) {
        if (canvas != null) {
            Paint paint = new Paint();
            paint.setARGB(255, 0, 50, 0);
            //canvas.drawLine(600, 0, canvas.getWidth(), safeZone, paint);
            canvas.drawLine(safeZone, 0, safeZone, canvas.getHeight(), paint);
        }
    }


    private void displayText(Canvas canvas, String text, int yHeight) {
        if (canvas != null && text != null) {
            Paint paint = new Paint();
            paint.setARGB(255, 0, 0, 0);
            canvas.drawText(text, this.getWidth() - 150, yHeight, paint);
        }
    }


    /**
     * This is the game update method. It iterates through all the objects
     * and calls their update method if they have one or calls specific
     * engine's update method.
     */
    public void update() {

        // will eventually check if sortee has been unsorted for too long
        List<Sortee> toRemove = new ArrayList<Sortee>();
        for ( Sortee sortee: sortees) {
            sortee.update(System.currentTimeMillis());
            if (sortee.isSafe()) {
                safeZoneTest = "A sortee has been sorted";
            }
            if (sortee.isDead()) {
                toRemove.add(sortee);
            }
        }
        sortees.removeAll(toRemove);
        numberOfSortees = Integer.toString(sortees.size());

        randomNewSortee(System.currentTimeMillis());

    }


    public void randomNewSortee(Long time) {
        nextRespawn = NEW_SORTEE_TIME - rndInt(0,1500);
        if (time > newSorteeTicker + nextRespawn) {
            newSorteeTicker = time;
            sortees.add( new Sortee(BitmapFactory.decodeResource(getResources(), R.drawable.dog_both),
                    rndInt(0,500), rndInt(0,400),  // initial position
                    25, 20,  // width and height of sprite
                    5, 2,    // FPS and number of frames in the animation
                    this.getRight() - SAFE_ZONE_WIDTH, System.currentTimeMillis()));   // Where the safe zone starts and when sortee created
        }
    }

    // Return an integer that ranges from min inclusive to max inclusive.
    static int rndInt(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }
}
