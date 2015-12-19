package one.almostd.sprong;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Nick on 2015-12-01.
 */
public class Ball {
    RectF rect;
    float xVelocity;
    float yVelocity;
    public float x;
    public float y;
    float ballWidth = 25;
    float ballHeight = 25;

    public Ball(int screenX, int screenY){

        x = screenX / 2;
        y = (screenY/2);

        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = 300;
        yVelocity = -600;

        // Place the ball in the centre of the screen at the bottom

        rect = new RectF(x, y , x + ballWidth, y + ballHeight);

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

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = rect.bottom - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = rect.left + ballWidth;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y - 50;
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

}
