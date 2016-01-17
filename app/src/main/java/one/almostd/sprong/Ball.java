package one.almostd.sprong;

import android.graphics.RectF;


/**
 * Created by Nick on 2015-12-01.
 */
public class Ball {
    RectF rect;
    float xVelocity;
    float yVelocity;
    public float x;
    public float y;
    float ballWidth;
    float ballHeight;
    public int lastPaddleHit = 0;
    public boolean isVisible = true;


    public Ball(int screenX, int screenY, int player){
        ballWidth = screenX/22;
        ballHeight = screenX/22;
        x = screenX/2 - (ballWidth / 2) ;
        if (player == 1) {
            y = (float) (screenY - screenY / 4.5);
            xVelocity = (int) (screenX/7.2);
            yVelocity = (float) (screenY/3.2);
        }
        else {
            y = (float) (screenY / 4.5);
            xVelocity = - (int) (screenX/7.2);
            yVelocity = (float) (- screenY/3.2);
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

    public void reset( int x, int y, boolean top){
        rect.left = x / 2;
        if (top){
            rect.top = (float) (y/4.5);
            xVelocity = - (int) (x/7.2);
        }
        else {
            rect.top = (float) (y - y / 4.5);
            xVelocity= (int) (x/7.2);
        }
        yVelocity=-yVelocity;
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

}
