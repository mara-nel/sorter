package com.learnknots.wesslnelson.Sorter.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;


/**
 * Created by wesslnelson on 5/18/16.
 */
public class Sortee {

    private final static int LIFE_SPAN = 5000; // # of ms until death
    public static final int STATE_ALIVE     = 0;    // sortee is younger than LIFE_SPAN
    public static final int STATE_DEAD      = 1;    // sortee is too old

    private Bitmap bitmap;      // the animation sequence
    private Rect sourceRect;    // the rectangle to be drawn from the animation bitmap
    private int frameNr;        // number of frames in animation
    private int currentFrame;   // the current frame
    private long frameTicker;   // the time of the last frame update
    private int framePeriod;    // milliseconds between each frame (1000/fps)
    private long birth;          // when a sortee is created
    private int lifeState;          // whether or not a sortee is alive

    private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
    private int spriteHeight;   // the height of the sprite

    private int x; // the X coordinate
    private int y; // the y coordinate
    private boolean touched; // true if sortee is touched/picked up

    private int safeZone; // x coordinate for which anything greater than is safe

    private Explosion explosion; // if dies outside of safezone then it explodes

    public Sortee(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount, int safeZone, long startTime) {
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
        this.safeZone = safeZone;
        this.birth = startTime;
        this.lifeState = STATE_ALIVE;
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

    public int getSafeZone() {
        return safeZone;
    }
    public void setSafeZone( int safeZone) {
        this.safeZone = safeZone;
    }

    public boolean isSafe() {
        if (getX() > getSafeZone()) {
            return true;
        } else {
            return false;
        }
    }

    public int getLifeState() {
        return lifeState;
    }

    public void setLifeState(int state) {
        this.lifeState = state;
    }

    // helper methods -------------------------
    public boolean isAlive() {
        return this.lifeState == STATE_ALIVE;
    }
    public boolean isDead() {
        return this.lifeState == STATE_DEAD;
    }

    public void update(long gameTime) {
        if (isAlive() || isSafe()) {
            if (gameTime > frameTicker + framePeriod) {
                frameTicker = gameTime;
                // increment the frame
                currentFrame++;
                if (currentFrame >= frameNr) {
                    currentFrame = 0;
                }
            }
            if (gameTime - birth >= LIFE_SPAN) {
                setLifeState(STATE_DEAD);
            }


            // define the rectangle to cut out sprite
            this.sourceRect.left = currentFrame * spriteWidth;
            this.sourceRect.right = this.sourceRect.left + spriteWidth;
        } else {
            runExplosion();
        }
    }

    public void draw(Canvas canvas) {
        // where to draw the sprite
        if (isAlive() || isSafe()) {
            Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
            canvas.drawBitmap(bitmap, sourceRect, destRect, null);
        } else if (explosion != null) {
            explosion.setX(getX());
            explosion.setY(getY());
            explosion.draw(canvas);
        }
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2))) {
            if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
                // sortee touched
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }
    }

    public void runExplosion() {
        if (explosion == null) {
            explosion = new Explosion(200, getX(), getY());
        } else {
            explosion.update();
        }
    }

}
