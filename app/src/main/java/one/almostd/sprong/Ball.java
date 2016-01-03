package one.almostd.sprong;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Nick on 2015-12-01.
 */
public class Ball {
    public RectF rect;
    float xVelocity;
    float yVelocity;
    public float x;
    public float y;
    float ballWidth;
    float ballHeight;


    public Ball(int screenX, int screenY, int player){
        ballWidth = 20;
        ballHeight = 20;
        x = screenX/2 ;
        if (player == 1) {
            y = screenY - screenY / 5;
            xVelocity = 200;
            yVelocity = 400;
        }
        else {
            y = screenY / 5;
            xVelocity = -200;
            yVelocity = -400;
        }
        rect = new RectF(x , y, x + ballWidth, y + ballHeight);
        // Start the ball travelling straight up at 100 pixels per second


        // Place the ball in the centre of the screen at the bottom



    }

    public RectF getRect(){

        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps );
        rect.top = rect.top + (yVelocity / fps );
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top + ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }



    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = rect.bottom - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = rect.left + ballWidth;
    }

    public void reset(int x, int y, boolean top){
        rect.left = x / 2;
        if (top){
            rect.top = y/5;
        }
        else {
            rect.top = y - y / 5;
        }
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

}
