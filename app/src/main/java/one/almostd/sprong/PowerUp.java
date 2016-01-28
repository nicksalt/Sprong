package one.almostd.sprong;

import android.graphics.Bitmap;

import java.util.Random;

/**
 * Created by Nick on 2016-01-10.
 */
public class PowerUp {
    /*I like to use private variables to make sure I dont mess anything up in other classes.
      By making variables private, then with a corresponding method I make sure I actually am sure to call it
     */
    private Bitmap powerUp;

    Random r = new Random();
    private int powerUpNum;
    private float x;
    private float  y;
    int yVelocity;
    public int paddle;
    private boolean visible = true;
    public boolean isActive;
    private long activateTime;

    public PowerUp (int screenY, float xOfBrick, float yOfBrick, int paddleHit, Bitmap multiBall,
                    Bitmap smallPaddle, Bitmap largePaddle, Bitmap reversePaddle, Bitmap bullet) {
        x = xOfBrick;
        y = yOfBrick;
        yVelocity = (int)(screenY / 3.2);
        paddle = paddleHit;
        //Randome powerup.
        powerUpNum = r.nextInt(5);

        switch (powerUpNum) {
            case 1:
                powerUp = smallPaddle;
                break;
            case 2:
                powerUp = largePaddle;
                break;
            case 3:
                powerUp = reversePaddle;
                break;
            case 4:
                powerUp = bullet;
                break;
            default:
                powerUp = multiBall;
                break;
        }
    }
    //Methods for public variables
    public Bitmap getPowerUp (){
        return powerUp;
    }

    public boolean isVisible(){
        return visible;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getCenterX(){
        return x + powerUp.getWidth()/2;
    }

    public float getBottomY(){
        return (y + powerUp.getHeight());
    }

    public void invisible(){
        visible = false;
    }

    public void unactivate(){
        isActive = false;
    }

    public void activate (long startTime){
        isActive = true;
        activateTime = startTime;
    }

    public int getPowerUpNum(){
        return powerUpNum;
    }
    //Last for 5 seconds so I save the activated time
    public long getActivateTime(){
        return activateTime;
    }

    //If paddle 1 it goes down, if paddle to it goes up.
    public void update (long fps){
        if (paddle == 1){
            y = (y + (yVelocity / fps));
        }
        else {
            y = (y - (yVelocity / fps));
        }

    }



}
