package one.almostd.sprong;

import android.graphics.RectF;

/**
 * Created by Nick on 2015-12-01.
 */
public class Paddle {

    //RectF is an object that holds four coordinates - just what we need
    private RectF rect;
    //How long and high our paddle will be
    public float length;
    public float height;
    boolean top;
    int screenX;
    int screenY;
    public boolean reversePaddle;
    //X is the far left of the rectangle which forms our paddle
    public float x;



    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenWidth, int screenHeight, int y, boolean bottom) {
        screenX = screenWidth;
        screenY = screenHeight;

        length = (float)(screenX/4.25);
        height = screenY/50;

        // Start paddle in roughly the screen centre
        x = ((int)((screenX / 2) - (length/2)));

        if (bottom) {
            top = false;
            rect = new RectF(x, y - height, x + length, y);
        }
        else {
            top = true;
            rect = new RectF(x, y, x + length, y + height);
        }
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
        update(0);

    }

    public void paddleShrinkReset() {
        if (length == (float)(screenX/4.25/2) ){
            length = (float)(screenX/4.25);
            update(0);
        }
    }

    public void setPaddleGrowReset() {
        if (length == (float) (screenX / 4.25 * 2)) {
            length = (float) (screenX / 4.25);
            update(0);
        }
    }


    public void update (float x){
        if (reversePaddle){
            x = - x;
        }
        rect.left = rect.left + x;
        rect.right = rect.left + length;
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
