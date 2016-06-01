package com.learnknots.wesslnelson.Sorter.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Long2;
import android.util.Log;

import com.learnknots.wesslnelson.Sorter.MainGamePanel;
import com.learnknots.wesslnelson.Sorter.R;


/**
 * Created by wesslnelson on 5/18/16.
 *
 * age is used to keep track of when a sortee should fade or explode
 * life of a sortee is used to know if stuff is still happening or is all done
 *
 */
public class Sortee {

    private static final String TAG = Sortee.class.getSimpleName();

    private static final int FADE_MILLISECONDS = 1500;//R.integer.fade_milliseconds;
    private static final int FADE_STEP = 120;//R.integer.fade_step;

    // Calculate our alpha step from our fade parameters
    private static final int ALPHA_STEP = 255 / (FADE_MILLISECONDS / FADE_STEP);
    private Paint alphaPaint = new Paint();
    private int currentAlpha = 255;


    private static final int LIFE_SPAN_UNSAFE   = 3500; // # of ms sortee survives while unsafe
    private static final int LIFE_SPAN_SAFE     = 1500; // # of ms sortee survives in safe zone
    public static final int STATE_YOUNG         = 0;    // sortee is younger than LIFE_SPAN
    public static final int STATE_OLD           = 1;    // sortee is too old
    public static final int STATE_SAFE          = 0;    // sortee is in a safe zone
    public static final int STATE_UNSAFE        = 1;    // sortee is not safe
    public static final int STATE_ALIVE         = 0;    // sortee is alive
    public static final int STATE_DEAD          = 1;    // sortee is dead
    public static final int LEFT                = MainGamePanel.LEFT;
    public static final int RIGHT               = MainGamePanel.RIGHT;


    private Bitmap bitmap;           // the animation sequence
    private Rect sourceRect;         // the rectangle to be drawn from the animation bitmap
    private int frameNr;             // number of frames in animation
    private int currentFrame;        // the current frame
    private long frameTicker;        // the time of the last frame update
    private int framePeriod;         // milliseconds between each frame (1000/fps)
    private long timeEnteringUnsafe; // when a sortee enters the unsafezone
    private long timeEnteringSafe;   // when a sortee enters the safezone
    private int ageState;            // whether or not a sortee is young or old
    private int safeState;           // whether or not a sortee is in a safezone
    private int lifeState;           // whether or not a sortee is all done
    private int safeSide;            // which side is this sortee safe in
    private int leftSortZone;        // the end of the left sort zone
    private int rightSortZone;       // the start of the right sort zone

    private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;   // the height of the sprite

    private int x; // the X coordinate
    private int y; // the y coordinate
    private boolean touched; // true if sortee is touched/picked up

    private Explosion explosion; // if dies outside of safezone then it explodes

    public Sortee(Bitmap bitmap, int x, int y, int width, int height, int fps,
                  int frameCount, int leftSortZone, int rightSortZone, long startTime, int safeSide) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        currentFrame = 0;
        frameNr = frameCount;
        spriteWidth = bitmap.getWidth() / frameCount; // all frames are the same width
        spriteHeight = bitmap.getHeight();
        sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
        framePeriod = 1000 / fps;
        frameTicker = 0l;
        this.leftSortZone = leftSortZone;
        this.rightSortZone = rightSortZone;
        this.timeEnteringUnsafe = startTime;
        this.ageState = STATE_YOUNG;
        this.safeState = STATE_UNSAFE;
        this.timeEnteringSafe = -1;
        this.lifeState = STATE_ALIVE;
        this.safeSide = safeSide;


    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public boolean isTouched() {
        return touched;
    }
    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public Rect getSourceRect() {
        return sourceRect;
    }
    public void setSourceRect(Rect sourceRect) {
        this.sourceRect = sourceRect;
    }

    public int getFrameNr() {
        return frameNr;
    }
    public void setFrameNr(int frameNr) {
        this.frameNr = frameNr;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getFramePeriod() {
        return framePeriod;
    }
    public void setFramePeriod(int framePeriod) {
        this.framePeriod = framePeriod;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }
    public void setSpriteWidth(int spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }
    public void setSpriteHeight(int spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    public int getLeftSortZone() { return leftSortZone; }
    public int getRightSortZone() { return rightSortZone; }

    // returns the x-coordinate marking start/end of correct sort zone
    public int getSafeZone() {
        if (leftSideSafe()) {
            return getLeftSortZone();
        } else if (rightSideSafe()) {
            return getRightSortZone();
        }
        // should never return this
        return -1;
    }

    // returns the x-coordinate marking start/end of wrong sort zone
    public int getDangerZone() {
        if (leftSideSafe()) {
            return getRightSortZone();
        } else if (rightSideSafe()) {
            return getLeftSortZone();
        }
        // should never return this
        return -1;
    }

    public long getTimeEnteringSafe() {
        return timeEnteringSafe;
    }
    public void setTimeEnteringSafe(long time) {
        this.timeEnteringSafe = time;
    }

    public long getTimeEnteringUnsafe() {
        return timeEnteringUnsafe;
    }
    public void setTimeEnteringUnsafe(long time) {
        this.timeEnteringUnsafe = time;
    }

    public int getAgeState() {
        return ageState;
    }
    public void setAgeState(int state) {
        this.ageState = state;
    }

    public int getSafeState() {
        return safeState;
    }
    public void setSafeState(int state) {
        this.safeState = state;
    }

    public int getLifeState() {
        return lifeState;
    }
    public void setLifeState(int state) {
        this.lifeState = state;
    }

    public int getSafeSide() { return safeSide; }
    public void setSafeSide(int side) { this.safeSide = side; }


    // helper methods -------------------------
    public boolean isYoung() {
        return this.ageState == STATE_YOUNG;
    }
    public boolean isOld() {
        return this.ageState == STATE_OLD;
    }
    public boolean isSafe() {
        return this.safeState == STATE_SAFE;
    }
    public boolean isUnsafe() {
        return this.safeState == STATE_UNSAFE;
    }
    public boolean isAlive() {
        return this.lifeState == STATE_ALIVE;
    }
    public boolean isDead() {
        return this.lifeState == STATE_DEAD;
    }
    public boolean leftSideSafe() { return this.safeSide == LEFT; }
    public boolean rightSideSafe() { return this.safeSide == RIGHT; }

    public boolean tooOldForSafe(Long gameTime) {
        return (gameTime - this.timeEnteringSafe >= LIFE_SPAN_SAFE);
    }
    public boolean tooOldForUnsafe(Long gameTime) {
        return (gameTime - this.timeEnteringUnsafe >= LIFE_SPAN_UNSAFE);
    }

    public void update(long gameTime) {
        if (isAlive()) {
            if (isYoung() && !inDanger()) {
                // handles animating the sprite
                if (gameTime > frameTicker + framePeriod) {
                    frameTicker = gameTime;
                    // increment the frame
                    currentFrame++;
                    if (currentFrame >= frameNr) {
                        currentFrame = 0;
                    }
                }
                // define the rectangle to cut out sprite
                this.sourceRect.left = currentFrame * spriteWidth;
                this.sourceRect.right = this.sourceRect.left + spriteWidth;


                checkSafe(gameTime);

                // checks if too old
                if (isUnsafe()) {
                    if (tooOldForUnsafe(gameTime)) {
                        setAgeState(STATE_OLD);
                    }
                } else if (isSafe()) {
                    if (tooOldForSafe(gameTime)) {
                        setAgeState(STATE_OLD);
                    }
                }


            } else if (isUnsafe() || inDanger()) {
                updateExplosion();
            } else if (isSafe()) {
                fadeOut();
            }
        }
    }

    public void draw(Canvas canvas) {
        // where to draw the sprite
        if (isYoung()  && !inDanger()) {
            Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
            canvas.drawBitmap(bitmap, sourceRect, destRect, null);

            if (inDanger() && explosion != null) {
                drawExplosion(canvas);
            }

        } else if (isUnsafe() && explosion != null) { // blow up old unsafe
            drawExplosion(canvas);

        } else if (isSafe()) { // fade out old safe
            Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
            canvas.drawBitmap(bitmap, sourceRect, destRect, alphaPaint);
        }
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventY >= (y) && (eventY <= (y + spriteHeight))) {
            if (eventX >= (x) && (eventX <= (x + spriteWidth))) {
                // sortee touched
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }
    }

    public void updateExplosion() {
        if (explosion == null) {
            explosion = new Explosion(200, getX(), getY());
        } else {
            if (explosion.isAlive()) {
                explosion.update();
            } else if (explosion.isDead()) {
                setLifeState(STATE_DEAD);
            }
        }
    }

    public void drawExplosion(Canvas canvas) {
        explosion.setX(getX());
        explosion.setY(getY());
        explosion.draw(canvas);
    }

    public void fadeOut() {
        if (currentAlpha >=20) {
            alphaPaint.setAlpha(currentAlpha);
            currentAlpha -= ALPHA_STEP;
        } else {
            setLifeState(STATE_DEAD);
        }

    }

    public void checkSafe(long gameTime) {
        // checks if in safe zone
        if (isUnsafe()) {
            if(leftSideSafe()) {
                if (getX() < getSafeZone()) {
                    setTimeEnteringSafe(gameTime);
                    setSafeState(STATE_SAFE);
                }
            } else if (rightSideSafe()){
                if (getX() > getSafeZone()) {
                    setTimeEnteringSafe(gameTime);
                    setSafeState(STATE_SAFE);
                }
            }

        } else if (isSafe()) { // checks to see if it moved out of the safe zone
            if( leftSideSafe() ) {
                if (getX() >= getSafeZone()) {
                    setTimeEnteringUnsafe(gameTime);
                    setSafeState(STATE_UNSAFE);
                }

            } else if ( rightSideSafe() ) {
                if (getX() <= getSafeZone()) {
                    setTimeEnteringUnsafe(gameTime);
                    setSafeState(STATE_UNSAFE);
                }
            }

        }
    }
    public boolean inDanger() {
        if(leftSideSafe()) {
            if(getX() > getDangerZone()) {
                return true;
            } else {
                return false;
            }
        } else {
            if(getX() < getDangerZone()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
