package one.almostd.sprong;

import android.graphics.Bitmap;

import java.util.Random;

/**
 * Created by Nick on 2016-01-10.
 */
public class PowerUp {

    private Bitmap powerUp;

    Random r = new Random();
    private int powerUpNum;
    private float x;
    private float  y;
    int yVelocity;
    public int paddle;
    private boolean isVisible = true;
    public boolean isActive = false;
    private long activateTime;

    public PowerUp (int screenY, float xOfBrick, float yOfBrick, int paddleHit, Bitmap multiball,
                    Bitmap smallPaddle, Bitmap largePaddle, Bitmap reversePaddle, Bitmap bullet) {
        x = xOfBrick;
        y = yOfBrick;
        yVelocity = (int)(screenY / 3.2);
        paddle = paddleHit;

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
                powerUp = multiball;
                break;
        }
    }

    public Bitmap getPowerUp (){
        return powerUp;
    }

    public boolean getVisibilty(){
        return isVisible;
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
        isVisible = false;
    }

    public void activate (long startTime){
        isActive = true;
        activateTime = startTime;
    }

    public int getPowerUpNum(){
        return powerUpNum;
    }

    public long getActivateTime(){
        return activateTime;
    }


    public void update (long fps){
        if (paddle == 1){
            y = (y + (yVelocity / fps));
        }
        else {
            y = (y - (yVelocity / fps));
        }

    }



}
