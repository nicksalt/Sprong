package one.almostd.sprong;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Nick on 2015-12-01.
 */
public class Brick {
    private RectF rect;
    Random r = new Random();

    private boolean isVisible;
    private int randomColour;

    public Brick(int row, int column, int width, int height){

        isVisible = true;

        int padding = 15;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
        randomColour = r.nextInt(5);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
    public int getRandommColour(){
        return randomColour;
    }
}
