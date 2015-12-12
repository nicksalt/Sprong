package one.almostd.sprong;


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
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


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

        private float mLastTouchX;

        // The players paddle
        Paddle paddle1;
        Paddle paddle2;

        // A ball
        Ball ball;

        // Up to 200 bricks
        Brick[] bricks = new Brick[72];
        int numBricks = 0;
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
            int brickHeight = screenY / 28;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 2; column < 8; column++) {
                for (int row = 8; row < 20; row++) {
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

                canvas.drawColor(Color.BLACK);

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
                //draw lines for touch area
                canvas.drawLine(0, (float) (screenY - (screenY / 24)), screenX, ((float) (screenY - (screenY / 24))), paint);
                canvas.drawLine(0, (float) (screenY / 24), screenX, (float) (screenY / 24), paint);

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
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        private final static int INVALID_POINTER_ID = -1;
        // The ‘active pointer’ is the one currently moving our object.
        private int mActivePointerId = INVALID_POINTER_ID;
        //private ScaleGestureDetector mScaleDetector;

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = MotionEventCompat.getActionMasked(ev);
            Log.d("action", " PASS");

            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                    Log.d("pointerIndex", " PASS");
                    mLastTouchX = MotionEventCompat.getX(ev, pointerIndex);
                    Log.d("mLastTouchX", " PASS");
                    //Save ID of this pointer
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    Log.d("mActivePointerId", " PASS");
                    break;
                }


                case MotionEvent.ACTION_MOVE: {
                    final int pointerIndex =
                            MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);

                    final float deltaX = x - mLastTouchX;

                    paddle1.update(deltaX, screenX, y, screenY);
                    paddle2.update(deltaX, screenX, y, screenY);

                    invalidate();

                    mLastTouchX = x;

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP:{
                    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                        mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    }
                    break;
                }
            }




            return true;
        }
    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Testing", "Resume");

        // Tell the gameView resume method to execute
        localMultiplayerView.resume();
    }
    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.


    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Testing: ", "Pause");

        // Tell the gameView pause method to execute
        localMultiplayerView.pause();
    }
}


// This is the end of the BreakoutGame class

// This is the end of the BreakoutGame class


