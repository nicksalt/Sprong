package one.almostd.sprong;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;


public class LocalMultiplayer extends Activity {
    /*Holds logic of the local multiplayer - like a motherboard of computer
    Will also respond to screen touches
    */
    LocalMultiplayerView localMultiplayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init localmultiplayerview and set it as the view
        localMultiplayerView = new LocalMultiplayerView(this);
        setContentView(localMultiplayerView);

    }


    // THis is a inner class - Implemnt runnable so we have A thread and can override the run method
    class LocalMultiplayerView extends SurfaceView implements Runnable {
        //This is the game thread
        Thread gameThread = null;

        //Need this to use canvas and paint
        SurfaceHolder ourHolder;

        //Boolean to set and unset when game is running
        volatile boolean playing;

        //Game is paused at the start
        boolean paused = true;

        Canvas canvas;
        Paint paint;

        //Tracks the game's frame rate
        long fps;

        //This is used to help caculate the fps
        private long timeThisFrame;

        public int screenX;
        public int screenY;

        // The players paddle
        Paddle paddle1;
        Paddle paddle2;

        // A ball
        Ball ball;

        // Up to 200 bricks
        Brick[] bricks = new Brick[72];
        int numBricks = 0;
        InitalX initalX;
        // The score
        int score = 0;

        // Lives
        int lives = 3;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public LocalMultiplayerView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();
            //bitmapBackground = BitmapFactory.decodeResource(this.getResources(), R.drawable.moon);
            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.


            screenX = size.x;
            screenY = size.y;

            paddle1 = new Paddle(screenX, screenY);
            paddle2 = new Paddle(screenX, screenY);

            // Create a ball
            ball = new Ball(screenX, screenY);


            createBricksAndRestart();

        }

        public void createBricksAndRestart() {

            // Put the ball back to the start
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 10;
            int brickHeight = screenY / 30;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 2; column < 8; column++) {
                for (int row = 9; row < 21; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            // if game over reset scores and lives
            if (lives == 0) {
                score = 0;
                lives = 3;
            }

        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                // Update the frame
                if (!paused) {
                    update();
                }

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        public void update() {


            //paddle1.update(1, 0);
            //paddle2.update(2,0);

            ball.update();

            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {

                if (bricks[i].getVisibility()) {

                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;

                    }
                }
            }

            // Check for ball colliding with paddle
            if (RectF.intersects(paddle1.getRect1(), ball.getRect())) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 30);
            }

            if (RectF.intersects(paddle2.getRect2(), ball.getRect())) {
                ball.reverseYVelocity();
                ball.clearObstacleY(35);
            }


            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > (screenY) &&
                    !RectF.intersects(paddle1.getRect1(), ball.getRect())) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);


                // Lose a life
                lives--;

                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }

            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0 && !RectF.intersects(ball.getRect(), paddle2.getRect2())) {
                ball.clearObstacleY(12);//was 12 as 12/10 = 1.2 & 1.2*25 = 30
                ball.reverseYVelocity();
                lives--;

            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(1);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 27);

            }

            // Pause if cleared screen
            if (score == numBricks * 10) {
                paused = true;
                createBricksAndRestart();
            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash

            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 0, 0, 0));


                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 0, 0, 255));

                // Draw the paddle
                canvas.drawRect(paddle1.getRect1(), paint);

                //Draw paddle 2
                canvas.drawRect(paddle2.getRect2(), paint);
                //            canvas.drawBitmap(bitmapBackground, 0, 100, paint);

                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the ball
                canvas.drawRect(ball.getRect(), paint);

                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 255, 0, 0));

                // Draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives + "   fps: " + fps, 10, 50, paint);

                // Has the player cleared the screen?
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }

                // Has the player lost?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                    createBricksAndRestart();
                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started theb
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }



        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            final int x = (int) ev.getRawX();
            final int y = (int) ev.getRawY();
            int deltaX;




            final int action = MotionEventCompat.getActionMasked(ev);


            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    initalX = new InitalX((int)ev.getRawX());


                case MotionEvent.ACTION_MOVE:
                    if (y > screenY / 2) {
                        deltaX = x - initalX.getInitalX() ;

                        paddle1.update(1, deltaX, screenX);
                    }
                    if (y <= screenY / 2) {
                        deltaX = x - initalX.getInitalX();
                        paddle2.update(2, deltaX, screenX);
                    }
                    initalX.resetX((int)ev.getRawX());


                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;

            }




            return true;
        }
    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        localMultiplayerView.resume();
    }
    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.


    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        localMultiplayerView.pause();
    }
}


// This is the end of the BreakoutGame class

// This is the end of the BreakoutGame class


