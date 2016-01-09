package one.almostd.sprong;

import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Nick on 2015-12-01.
 */
public class Paddle {

    //RectF is an object that holds four coordinates - just what we need
    private RectF rect;
    //How long and high our paddle will be
    public float length;
    public float height;

    //X is the far left of the rectangle which forms our paddle
    public float x;



    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY, int y, boolean bottom) {
        length = (float)(screenX/4.25);
        height = screenY/50;
        Log.d("ScreenX ", Integer.toString(screenX));

        // Start paddle in roughly the screen centre
        x = ((int)((screenX / 2) - (length/2)));

        if (bottom) {
            rect = new RectF(x, y - height, x + length, y);
        }
        else {
            rect = new RectF(x, y, x + length, y + height);
        }
    }



    public RectF getRect() {
        return rect;
    }




    public void update ( float x, int screenX){
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
