package one.almostd.sprong;

import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Nick on 2015-12-01.
 */
public class Paddle {

    //RectF is an object that holds four coordinates - just what we need
    private RectF rect1;
    private RectF rect2;
    //How long and high our paddle will be
    public final float length;
    public final float height;

    //X is the far left of the rectangle which forms our paddle
    public float x1;
    public float x2;

    // Y is the top coordinate
    public float y1;
    public float y2;


    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY) {
        length = (float)(screenX/4.25);
        height = screenY/50;
        Log.d("ScreenX ", Integer.toString(screenX));

        // Start paddle in roughly the sceen centre
        x1 = ((int)((screenX / 2) - (length/2)));
        y1 = screenY - (screenY/24) - height;
        x2 = x1;
        y2 = screenY/24;


        rect1 = new RectF(x1, y1, x1 + length, y1 + height);
        rect2 = new RectF(x2, y2, x2 + length, y2 + height);


    }



    public RectF getRect1() {
        return rect1;
    }

    public RectF getRect2() {
        return rect2;
    }



    public void update ( float x, int screenX, float y, int screenY){
        if (y > screenY / 2){
            rect1.left = rect1.left + x;
            rect1.right = rect1.left + length;
            if (rect1.left < 0 ){
                rect1.left=0;
                rect1.right = rect1.left + length;
            }
            if (rect1.right > screenX){
                rect1.left = screenX - length;
                rect1.right = screenX;
            }
        }
        else{
            rect2.left = rect2.left + x;
            rect2.right = rect2.left + length;
            if (rect2.left < 0 ){
                rect2.left=0;
                rect2.right = rect2.left + length;
            }
            if (rect2.right > screenX){
                rect2.left = screenX - length;
                rect2.right = screenX;
            }
        }


    }

}
