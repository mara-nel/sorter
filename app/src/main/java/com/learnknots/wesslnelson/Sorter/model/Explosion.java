package com.learnknots.wesslnelson.Sorter.model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by wesslnelson on 5/21/16.
 *
 * following example from javacodegeeks.com
 */
public class Explosion {

    private static final String TAG = Explosion.class.getSimpleName();

    public static final int STATE_ALIVE     = 0;    // at least 1 particle is alive
    public static final int STATE_DEAD      = 1;    // all particles are dead

    private Particle[] particles;           // particles in the explosion
    private int x, y;                       // the explosion's origin
    private int size;                       // number of particles
    private int state;                      // whether it's still active or not


    public Explosion(int particleNr, int x, int y) {
        Log.d(TAG, "Explosion created at " + x + "," + y);
        this.state = STATE_ALIVE;
        this.particles = new Particle[particleNr];
        for (int i = 0; i < this.particles.length; i++) {
            Particle p = new Particle(x, y);
            this.particles[i] = p;
        }
        this.size = particleNr;
    }
    public Particle[] getParticles() {
        return particles;
    }
    public void setParticles(Particle[] particles) {
        this.particles = particles;
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

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    // helper methods -------------------------
    public boolean isAlive() {
        return this.state == STATE_ALIVE;
    }
    public boolean isDead() {
        return this.state == STATE_DEAD;
    }

    public void update() {
        if (this.state != STATE_DEAD) {
            boolean isDead = true;
            for (int i = 0; i < this.particles.length; i++) {
                if (this.particles[i].isAlive()) {
                    this.particles[i].update();
                    isDead = false;
                }
            }
            if (isDead)
                this.state = STATE_DEAD;
        }
    }

    public void update(Rect container) {
        if (this.state != STATE_DEAD) {
            boolean isDead = true;
            for (int i = 0; i < this.particles.length; i++) {
                if (this.particles[i].isAlive()) {
                    this.particles[i].update(container);
//					this.particles[i].update();
                    isDead = false;
                }
            }
            if (isDead)
                this.state = STATE_DEAD;
        }
    }

    public void draw(Canvas canvas) {
        for(int i = 0; i < this.particles.length; i++) {
            if (this.particles[i].isAlive()) {
                this.particles[i].draw(canvas);
            }
        }
    }
}

