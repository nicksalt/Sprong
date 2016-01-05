package one.almostd.sprong;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


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

        private float mLastTouchX1;
        private float mLastTouchX2;

        // The players paddle
        Paddle paddle1;
        Paddle paddle2;

        Bitmap background;




        // A ball
        Ball[] balls = new Ball[10];
        int numBalls;

        // Up to 200 bricks
        Brick[] bricks = new Brick[72];
        int numBricks;
        // The score
        int score1;
        int score2;



        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public LocalMultiplayerView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();
            background = BitmapFactory.decodeResource(this.getResources(), R.drawable.deepfield16x9);
            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);



            screenX = size.x;
            screenY = size.y;
            Log.d("X ", Integer.toString(screenX));
            Log.d("Y ", Integer.toString(screenY));
            paddle1 = new Paddle(screenX, screenY);
            paddle2 = new Paddle(screenX, screenY);




            createBricksAndRestart();

        }


        public void createBricksAndRestart() {

            balls[0] = new Ball(screenX, screenY, 1);
            balls[1] = new Ball (screenX, screenY, 2);
            numBalls = 2;


            int brickWidth = screenX / 10;
            int brickHeight = screenY / 25;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 2; column < 8; column++) {
                for (int row = 7; row < 19; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
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
                /*Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
                Canvas c = new Canvas(bitmap);*/
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



            for (int i = 0; i < numBalls; i++) {
                balls[i].update(fps);
            }

            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {

                if (bricks[i].getVisibility()) {
                    for (int k = 0; k < numBalls; k++){
                        Ball ball = balls[k];
                        if (RectF.intersects(ball.getRect(), bricks[i].getRect())) {
                            Point top = new Point((int)ball.getRect().centerX(),(int)ball.getRect().top);
                            Point bottom = new Point((int)ball.getRect().centerX(),(int)ball.getRect().bottom);
                            Point right = new Point((int)ball.getRect().right ,(int)ball.getRect().centerY());
                            Point left = new Point((int)ball.getRect().left ,(int)ball.getRect().centerY());

                            if (bricks[i].getRect().contains(top.x,top.y)){
                                ball.reverseYVelocity();
                                bricks[i].setInvisible();
                            }
                            if (bricks[i].getRect().contains(bottom.x,bottom.y)){
                                ball.reverseYVelocity();
                                bricks[i].setInvisible();
                            }

                            if (bricks[i].getRect().contains(right.x,right.y)){
                                ball.reverseXVelocity();
                                bricks[i].setInvisible();
                            }

                            if (bricks[i].getRect().contains(left.x,left.y)){
                                ball.reverseXVelocity();
                                bricks[i].setInvisible();
                            }

                            if (ball.lastPaddleHit == 1){
                                score1 += 10;
                            }
                            else {
                                score2+=10;
                            }

                        }
                    }
                }
            }
            for (int k = 0; k < numBalls; k++) {
                Ball ball = balls[k];


                // Check for ball colliding with paddle 1
                if (RectF.intersects(paddle1.getRect1(), ball.getRect())) {
                    ball.reverseYVelocity();
                    ball.clearObstacleY(paddle1.getRect1().top);
                    ball.lastPaddleHit = 1;
                }

                if (RectF.intersects(paddle2.getRect2(), ball.getRect())) {
                    ball.reverseYVelocity();
                    ball.clearObstacleY(paddle2.getRect2().bottom + ball.ballHeight);
                    ball.lastPaddleHit = 2;
                }


                // Bounce the ball back when it hits the bottom of screen
                if (ball.getRect().bottom > (screenY - screenY / 24) &&
                        !RectF.intersects(paddle1.getRect1(), ball.getRect())) {
                    ball.reverseYVelocity();
                    ball.clearObstacleY(screenY - screenY / 24 - 2);



                }

                // Bounce the ball back when it hits the top of screen
                if (ball.getRect().top < (screenY / 24) && !RectF.intersects(ball.getRect(), paddle2.getRect2())) {
                    ball.clearObstacleY(paddle2.getRect2().top + 25);
                    ball.reverseYVelocity();

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
            }
            // Pause if cleared screen
            if (score1+score2 == numBricks * 10) {
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

                canvas.drawColor(Color.WHITE);
                Rect dest = new Rect(0, 0, getWidth(), getHeight());
                Paint paint = new Paint();
                paint.setFilterBitmap(true);
                canvas.drawBitmap(background, null, dest, paint);
                // Choose the brush color for drawing
                paint.setColor(Color.BLUE);

                paint.setTextSize(24);
                canvas.drawText("Fps: " + " " + Float.toString(fps), 10, 50, paint);

                // Draw the paddle
                canvas.drawRect(paddle1.getRect1(), paint);

                //Draw paddle 2
                canvas.drawRect(paddle2.getRect2(), paint);

                paint.setColor(Color.WHITE);

                // Draw the ball
                for (int k = 0; k < numBalls; k++) {
                    Ball ball = balls[k];
                    canvas.drawOval(ball.getRect(), paint);
                }
                //draw lines for touch area
                canvas.drawLine(0, (float) (screenY - (screenY / 24)), screenX, ((float) (screenY - (screenY / 24))), paint);
                canvas.drawLine(0, (float) (screenY / 24), screenX, (float) (screenY / 24), paint);




                // Draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        int num = bricks[i].getRandommColour();
                        switch (num){
                            case 1:
                                paint.setColor(Color.BLUE);
                                break;
                            case 2:
                                paint.setColor(Color.GREEN);
                                break;
                            case 3:
                                paint.setColor(Color.MAGENTA);
                                break;
                            case 4:
                                paint.setColor(Color.WHITE);
                                break;
                            default:
                                paint.setColor(Color.RED);
                        }
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                paint.setTextSize((int)(canvas.getHeight()/22.5));
                canvas.drawText("Score: " + " " + Integer.toString(score1), 10, screenY - 10, paint);
                canvas.rotate(180, screenX - 10, 10);
                canvas.drawText("Score: " + " " + Integer.toString(score2), screenX - 10, 10, paint);
                //canvas.restore();


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

        private static final int INVALID_POINTER_ID = -1;
        private int activePointer = INVALID_POINTER_ID;
        private int newPointer = INVALID_POINTER_ID;

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            switch (MotionEventCompat.getActionMasked(e)) {
                case MotionEvent.ACTION_DOWN: {
                    paused=false;
                    final int pointerIndex = MotionEventCompat.getActionIndex(e);
                    // Remember where we started (for dragging)
                    mLastTouchX1 = MotionEventCompat.getX(e, pointerIndex);
                    activePointer = MotionEventCompat.getPointerId(e, 0);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (activePointer != INVALID_POINTER_ID) {
                        float x = MotionEventCompat.getX(e, activePointer);
                        float y = MotionEventCompat.getY(e, activePointer);
                        float deltaX = x - mLastTouchX1;
                        paddle1.update(deltaX, screenX, y, screenY);
                        paddle2.update(deltaX, screenX, y, screenY);
                        mLastTouchX1 = x;
                    }
                    if (newPointer == 1) {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(e, newPointer);

                        float x = MotionEventCompat.getX(e, pointerIndex);
                        float y = MotionEventCompat.getY(e, pointerIndex);
                        float deltaX = x - mLastTouchX2;
                        paddle1.update(deltaX, screenX, y, screenY);
                        paddle2.update(deltaX, screenX, y, screenY);
                        mLastTouchX2 = x;

                    }
                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {
                    int newPointerIndex = MotionEventCompat.getActionIndex(e);
                    if (newPointerIndex == 1) {
                        newPointer = MotionEventCompat.getPointerId(e, newPointerIndex);
                        mLastTouchX2 = MotionEventCompat.getX(e, newPointerIndex);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    activePointer = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    activePointer = INVALID_POINTER_ID;
                    break;
                }
                case MotionEvent.ACTION_POINTER_UP: {
                    if (newPointer == 1) {
                        newPointer = INVALID_POINTER_ID;
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


