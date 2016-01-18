package one.almostd.sprong;

import android.graphics.RectF;

/**
 * Created by Nick on 2016-01-18.
 */
public class PaddleAI {

    //RectF is an object that holds four coordinates - just what we need
    private RectF rect;
    //How long and high our paddle will be
    public float length;
    public float height;
    int screenX;
    int screenY;
    float xVelocity;
    public boolean reversePaddle;
    //X is the far left of the rectangle which forms our paddle
    public float x;

    public PaddleAI(int screenWidth, int screenHeight, int y){
        screenX = screenWidth;
        screenY = screenHeight;

        xVelocity = screenWidth;
        length = (float)(screenX/4.25);
        height = screenY/50;

        // Start paddle in roughly the screen centre
        x = ((int)((screenX / 2) - (length/2)));

        rect = new RectF(x, y, x + length, y + height);

    }

    public RectF getRect() {
        return rect;
    }

    public void setPaddleShrink (){
        length = (float)(screenX/4.25/2);
        rect.right = rect.left + length;

    }

    public void setPaddleGrow() {
        length = (float)(screenX/4.25 * 2);
        rect.right = rect.left + length;

    }

    public void paddleShrinkReset() {
        if (length == (float)(screenX/4.25/2) ){
            length = (float)(screenX/4.25);
            rect.right = rect.left + length;
        }
    }

    public void setPaddleGrowReset() {
        if (length == (float) (screenX / 4.25 * 2)) {
            length = (float) (screenX / 4.25);
            rect.right = rect.left + length;
        }
    }


    public void update (float x, long fps){
        if(rect.right < x) {
            rect.left = rect.left + xVelocity / fps;
            rect.right = rect.left + length;
        }
        if (rect.left > x){
            rect.left = rect.left - xVelocity / fps;
            rect.right = rect.left + length;

        }
        if (rect.left < 0 ){
            rect.left=0;
            rect.right = rect.left + length;
        }
        if (rect.right > screenX){
            rect.left = screenX - length;
            rect.right = screenX;
        }
    }

}


