package one.almostd.sprong;

import android.graphics.RectF;

/**
 * Created by Nick on 2015-12-01.
 */
public class Paddle {

    //RectF is an object that holds four coordinates - just what we need
    private RectF rect1;
    private RectF rect2;
    //How long and high our paddle will be
    private float length;
    private float height;

    //X is the far left of the rectangle which forms our paddle
    public float x1;
    public float x2;

    // Y is the top coordinate
    public float y1;
    public float y2;

    // This will hold the pixels per second speedthat the paddle will move
    private float paddleSpeed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY) {
        // 130 pixels wide and 20 pixels high
        length = 200;
        height = 20;

        // Start paddle in roughly the sceen centre
        x1 = screenX / 2;
        y1 = screenY - 20;
        x2 = screenX / 2;
        y2 = 0;


        rect1 = new RectF(x1, y1, x1 + length, y1 + height);
        rect2 = new RectF(x2, y2, x2 + length, y2 + height);

        // How fast is the paddle in pixels per second
        paddleSpeed = 500;
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    public RectF getRect(int paddle) {
        if (paddle == 1) {
            return rect1;
        } else {
            return rect2;
        }
    }


    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state) {

        paddleMoving = state;
    }


    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps, int screenX, int paddle) {

        if (paddleMoving == LEFT) {
            if (paddle == 1) {
                x1 = x1 - paddleSpeed / fps;
                if (x1 < -1) {
                    paddleMoving = STOPPED;
                }
            }
            if (paddle == 2) {
                x2 = x2 - paddleSpeed / fps;
                if (x2 < -1) {
                    paddleMoving = STOPPED;
                }
            }
        }

        if (paddleMoving == RIGHT) {
            if (paddle == 1) {
                x1 = x1 + paddleSpeed / fps;
                if (x1 + length > (screenX + 1)) {
                    paddleMoving = STOPPED;
                }
            }
            if (paddle == 2) {
                x2 = x2 + paddleSpeed / fps;
                if (x2 + length > (screenX + 1)) {
                    paddleMoving = STOPPED;
                }
            }
        }


        rect1.left = x1;
        rect1.right = x1 + length;
        rect2.left = x2;
        rect2.right = x2 + length;



    }

}
