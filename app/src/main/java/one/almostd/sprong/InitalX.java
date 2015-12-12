package one.almostd.sprong;

/**
 * Created by Nick on 2015-12-10.
 */
public class InitalX {
    public float initalX;
    public boolean bottomHalf;
    public InitalX (float x, float y, float screenY){
        initalX = x;
        if (y > screenY/2){
            bottomHalf = true;
        }
        else {
            bottomHalf = false;
        }
    }

    public float getInitalX(){
        return initalX;
    }

    public void resetX(float x) {
        initalX = x;
    }
}

