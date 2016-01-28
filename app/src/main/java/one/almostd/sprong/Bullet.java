package one.almostd.sprong;


/**
 * Created by Nick on 2016-01-12.
 */
public class Bullet {
    //Initialize variable
    private boolean active;
    public boolean up;
    private int yVelocity;
    private float x;
    private float y;
    int bulletNum;
    long startTime;

    public Bullet (int screenY, float xofPaddle, float yofPaddle, int num, long start){
        yVelocity = screenY ;
        x = xofPaddle;
        y = yofPaddle;
        //Five bullets are initialized during powerup
        bulletNum = num;
        //Keeps track of when to shoot the bullets
        startTime = start;
        active=true;
        //Checks if the paddle is the top paddle or bottom, Java shorthand
        up = y > screenY / 2;
    }

    public boolean isActive(){
        return active;
    }

    public void unActive(){
        active= false;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void update (long fps) {
        switch (bulletNum) {
            //Each of the five bullets are shot off 0.2 seconds after one another
            case 0: {
                if (up) {
                    y = y - (yVelocity / fps);
                } else {
                    y = y + (yVelocity / fps);
                }
                break;
            }
            case 1: {
                if (System.currentTimeMillis() - startTime > 200){
                    if (up) {
                        y = y - (yVelocity / fps);
                    } else {
                        y = y + (yVelocity / fps);
                    }
                }
                break;
            }
            case 2: {
                if (System.currentTimeMillis() - startTime > 400){
                    if (up) {
                        y = y - (yVelocity / fps);
                    } else {
                        y = y + (yVelocity / fps);
                    }
                }
                break;
            }
            case 3: {
                if (System.currentTimeMillis() - startTime > 600){
                    if (up) {
                        y = y - (yVelocity / fps);
                    } else {
                        y = y + (yVelocity / fps);
                    }
                }
                break;
            }
            case 4: {
                if (System.currentTimeMillis() - startTime > 800){
                    if (up) {
                        y = y - (yVelocity / fps);
                    } else {
                        y = y + (yVelocity / fps);
                    }
                }
                break;
            }
            case 5: {
                if (System.currentTimeMillis() - startTime > 1000){
                    if (up) {
                        y = y - (yVelocity / fps);
                    } else {
                        y = y + (yVelocity / fps);
                    }
                }
                break;
            }
        }
    }



}
