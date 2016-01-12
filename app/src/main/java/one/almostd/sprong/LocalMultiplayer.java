package one.almostd.sprong;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
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

        int gameTop;
        int gameBottom;

        private float mLastTouchX1;
        private float mLastTouchX2;

        VelocityTracker mVelocityTracker1 = null;
        VelocityTracker mVelocityTracker2 = null;
        long xVelocity1;
        long xVelocity2;

        // The players paddle
        Paddle paddle1;
        Paddle paddle2;

        Bitmap background;

        Bitmap multiball;
        Bitmap smallPaddle;
        Bitmap largePaddle;
        Bitmap reversePaddle;
        Bitmap bullet;




        // A ball
        Ball[] balls = new Ball[72];
        int numBalls;

        // Up to 200 bricks
        Brick[] bricks = new Brick[72];
        int numBricks;

        PowerUp[] powerUps = new PowerUp[72];
        int numPowerUps;
        // The score
        public int score1;
        public int score2;

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean fpsBackground = myPrefs.getBoolean("fpsBackground", true);
        int fpsLow;
        int bricksInvisible;




        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public LocalMultiplayerView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);



            screenX = size.x;
            screenY = size.y;

            gameTop = screenY / 10;
            gameBottom = screenY - screenY/10;
            paddle1 = new Paddle(screenX, screenY, gameBottom, true);
            paddle2 = new Paddle(screenX, screenY, gameTop, false);



            imageDownloader();
            createBricksAndRestart();

        }

        public void imageDownloader() {
            background = BitmapFactory.decodeResource(this.getResources(), R.drawable.deepfield16x9);
            multiball = BitmapFactory.decodeResource(this.getResources(), R.drawable.powerup);
            smallPaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.smallpaddle);
            largePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.largepaddle);
            reversePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.reverse);
            bullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet);

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
                    bricks[numBricks] = new Brick(screenX, row, column, brickWidth, brickHeight);

                    numBricks++;
                }
            }



        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                //Insert

                // Update the frame
                // Update the frame
                if (!paused) {
                    update();
                    if (fps<18 && fpsBackground){
                        fpsLow+=1;
                        if (fpsLow >= 5) {
                            fpsBackground = false;
                            SharedPreferences.Editor e = myPrefs.edit();
                            e.putBoolean("fpsBackground", fpsBackground);
                            e.apply();
                            startActivity(new Intent(LocalMultiplayer.this, BackgroundMessage.class));
                        }
                    }
                    else {
                        fpsLow = 0;
                    }

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
            //Always update the ball
            for (int i = 0; i < numBalls; i++) {
                balls[i].update(fps);
            }

            // Check for ball colliding with a brick
            ballCollision();
            brickCollision();
            powerUpUpdate();


            if (numBricks==bricksInvisible){
                endGame();
            }

        }

        public void brickCollision(){
            for (int i = 0; i < numBricks; i++) {
                brickloop:
                if (bricks[i].getVisibility()) {
                    for (int k = 0; k < numBalls; k++) {
                        Ball ball = balls[k];
                        //if (RectF.intersects(ball.getRect(), bricks[i].getRect())) {
                        Point top = new Point((int) ball.getRect().centerX(), (int) ball.getRect().top);
                        Point bottom = new Point((int) ball.getRect().centerX(), (int) ball.getRect().bottom);
                        Point right = new Point((int) ball.getRect().right, (int) ball.getRect().centerY());
                        Point left = new Point((int) ball.getRect().left, (int) ball.getRect().centerY());

                        if (bricks[i].getRect().contains(top.x, top.y) ||
                                bricks[i].getRect().contains(bottom.x, bottom.y)) {
                            ball.reverseYVelocity();
                            bricks[i].setInvisible();
                            switch (ball.lastPaddleHit) {
                                case 1:
                                    score1 += 10;
                                    break;
                                case 2:
                                    score2 += 10;
                                    break;
                                default:
                                    break;
                            }
                            bricksInvisible += 1;
                            if (bricks[i].getRandommColour() == 0) {
                                powerUps[numPowerUps] = new PowerUp(screenY,bricks[i].getRect().left,
                                        bricks[i].getRect().top, ball.lastPaddleHit, multiball,
                                        smallPaddle, largePaddle, reversePaddle, bullet);
                                numPowerUps += 1;
                            }

                            break brickloop;
                        }

                        if (bricks[i].getRect().contains(right.x, right.y) ||
                                bricks[i].getRect().contains(left.x, left.y)) {
                            ball.reverseXVelocity();
                            bricks[i].setInvisible();
                            switch (ball.lastPaddleHit) {
                                case 1:
                                    score1 += 10;
                                    break;
                                case 2:
                                    score2 += 10;
                                    break;
                                default:
                                    break;
                            }
                            bricksInvisible += 1;
                            if (bricks[i].getRandommColour() == 0) {
                                powerUps[numPowerUps] = new PowerUp(screenY, bricks[i].getRect().left,
                                        bricks[i].getRect().top, ball.lastPaddleHit, multiball,
                                        smallPaddle, largePaddle, reversePaddle, bullet);

                                numPowerUps += 1;
                            }
                            break brickloop;
                        }

                    }
                }
            }
        }

        public void ballCollision () {
            for (int k = 0; k < numBalls; k++) {
                Ball ball = balls[k];
                if (ball.isVisible) {


                    // Check for ball colliding with paddle 1
                    if (RectF.intersects(paddle1.getRect(), ball.getRect())) {
                        ball.reverseYVelocity();
                        ball.clearObstacleY(paddle1.getRect().top);
                        ball.lastPaddleHit = 1;
                        ball.xVelocity += (xVelocity1 / 15);
                    }

                    if (RectF.intersects(paddle2.getRect(), ball.getRect())) {
                        ball.reverseYVelocity();
                        ball.clearObstacleY(paddle2.getRect().bottom + ball.ballHeight);
                        ball.lastPaddleHit = 2;
                        ball.xVelocity += (xVelocity2 / 15);
                    }


                    // Bounce the ball back when it hits the bottom of screen
                    if (ball.getRect().bottom > gameBottom &&
                            !RectF.intersects(paddle1.getRect(), ball.getRect())) {
                        ball.reset(screenX, screenY, true);
                        score2 += 10;
                        if (k > 1) {
                            ball.isVisible = false;
                        }

                    }

                    // Bounce the ball back when it hits the top of screen
                    if (ball.getRect().top < gameTop &&
                            !RectF.intersects(ball.getRect(), paddle2.getRect())) {
                        ball.reset(screenX, screenY, false);
                        score1 += 10;

                    }

                    // If the ball hits left wall bounce
                    if (ball.getRect().left < 0) {
                        ball.reverseXVelocity();
                        ball.clearObstacleX(2);
                    }

                    // If the ball hits right wall bounce
                    if (ball.getRect().right > screenX) {
                        ball.reverseXVelocity();
                        ball.clearObstacleX(screenX - ball.ballWidth);

                    }
                }
            }
        }

        public void powerUpUpdate() {
            for (int i = 0; i < numPowerUps; i++) {
                PowerUp powerUp = powerUps[i];
                if (powerUp.getVisibilty()) {
                    powerUp.update(fps);
                    if (paddle1.getRect().contains(powerUp.getCenterX(), powerUp.getBottomY())) {
                        powerUp.invisible();
                        powerUp.activate(System.currentTimeMillis());
                        if (paddle2.getRect().contains(powerUp.getCenterX(), powerUp.getY())) {
                            powerUp.invisible();
                            powerUp.activate(System.currentTimeMillis());
                        }
                        if (powerUp.getBottomY() > gameBottom) {
                            powerUp.invisible();
                        }

                        if (powerUp.getY() < gameTop) {
                            powerUp.invisible();
                        }
                    }
                    activatePowerUp(powerUp);
                }
            }
        }
        public void activatePowerUp(PowerUp powerUp){
            if (powerUp.isActive) {
                long startTime = powerUp.getActivateTime();
                switch (powerUp.getPowerUpNum()) {
                    case 1: { //small paddle
                        if (powerUp.paddle == 1) {
                            paddle1.setPaddleShrink();
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle1.paddleShrinkReset();
                                powerUp.isActive = false;
                            }
                        }
                        else {
                            paddle2.setPaddleShrink();
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle2.paddleShrinkReset();
                                powerUp.isActive = false;
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (powerUp.paddle == 1) {
                            paddle1.setPaddleGrow();
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle1.setPaddleGrowReset();
                                powerUp.isActive = false;
                            }
                        } else {
                            paddle2.setPaddleGrow();
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle2.setPaddleGrowReset();
                                powerUp.isActive = false;
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (powerUp.paddle == 1) {
                            paddle1.reversePaddle = true;
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle1.reversePaddle = false;
                                powerUp.isActive = false;
                            }
                            break;
                        }
                        else {
                            paddle2.reversePaddle = true;
                            if (System.currentTimeMillis() - startTime >= 5000) {
                                paddle2.reversePaddle = false;
                                powerUp.isActive = false;
                            }
                            break;
                        }
                    }
                    case 0: {
                        balls[numBalls] = new Ball(screenX, screenY, powerUp.paddle);
                        numBalls++;
                        powerUp.isActive=false;

                    }
                }
            }
        }


        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash

            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                if (fpsBackground) {
                    RectF dest = new RectF(0, 0, getWidth(), getHeight());
                    paint.setFilterBitmap(true);
                    canvas.drawBitmap(background, null, dest, paint);
                }
                else {
                    canvas.drawColor(Color.BLACK);
                }

                paint.setColor(Color.BLUE);
                paint.setTextSize(24);


                // Draw the paddle
                canvas.drawRect(paddle1.getRect(), paint);

                //Draw paddle 2
                canvas.drawRect(paddle2.getRect(), paint);

                paint.setColor(Color.WHITE);

                // Draw the ball
                for (int k = 0; k < numBalls; k++) {
                    Ball ball = balls[k];
                    if (ball.isVisible) {
                        canvas.drawOval(ball.getRect(), paint);
                    }
                }
                //draw lines for touch area
                canvas.drawLine(0, gameBottom, screenX, gameBottom, paint);
                canvas.drawLine(0, gameTop, screenX, gameTop, paint);




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
                                paint.setColor(Color.RED);
                                break;
                            case 5:
                                paint.setColor(getResources().getColor(R.color.orange));
                                break;
                            case 6:
                                paint.setColor(getResources().getColor(R.color.dark_green));
                                break;
                            case 7:
                                paint.setColor(getResources().getColor(R.color.light_blue));
                                break;
                            case 8:
                                paint.setColor(getResources().getColor(R.color.purple));
                                break;
                            case 9:
                                paint.setColor(getResources().getColor(R.color.dark_pink));
                                break;
                            default:
                                paint.setColor(Color.YELLOW);
                        }
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }
                for (int i = 0; i<numPowerUps; i++){
                    PowerUp powerUp = powerUps[i];
                    if(powerUp.getVisibilty()) {
                        canvas.drawBitmap(powerUp.getPowerUp(), powerUp.getX(), powerUp.getY(), paint);
                    }
                }
                if (paused){
                    Paint textPaint = new Paint();
                    textPaint.setTextSize(100);
                    textPaint.setColor(Color.WHITE);

                    int xPos = (int)(canvas.getWidth() / 14.4);
                    int yPos = screenY/2 + (3* screenX / 25) ;
                    canvas.rotate(270, xPos, yPos);
                    //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

                    canvas.drawText("Paused", xPos, yPos, textPaint);
                    canvas.rotate(90, xPos, yPos);
                }

                paint.setTextSize(canvas.getHeight()/20);
                canvas.drawText(Integer.toString(score1), 10, screenY - 10, paint);
                canvas.rotate(180, screenX - 10, 10);
                canvas.drawText(Integer.toString(score2), screenX - 10, 10, paint);
                //canvas.restore();


                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void endGame(){
            paused=true;
            SharedPreferences.Editor e = myPrefs.edit();
            e.putInt("score1", score1); // add or overwrite someValue
            e.putInt("score2", score2); // add or overwrite someValue
            e.apply(); // this saves to disk and notifies observers
            startActivity(new Intent(LocalMultiplayer.this, LmGameOver.class));
            finish();
        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            paused = true;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
            }

        }

        // If SimpleGameEngine Activity is started theb
        // start our thread.
        public void resume() {

            playing = true;
            gameThread = new Thread(this);
            gameThread.start();


        }

        private final int INVALID_POINTER_ID = -1;
        private int primaryPointer = INVALID_POINTER_ID;
        private int secondPointer = INVALID_POINTER_ID;
        private boolean touch1Bottom;
        private boolean touch2Bottom;


        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int index = MotionEventCompat.getActionIndex(e);
            int pointerId = MotionEventCompat.getPointerId(e, index);
            switch (MotionEventCompat.getActionMasked(e)) {

                case MotionEvent.ACTION_DOWN: {
                    paused=false;
                    // Remember where we started (for dragging)
                    mLastTouchX1 = MotionEventCompat.getX(e, index);
                    primaryPointer = pointerId;
                    touch1Bottom = MotionEventCompat.getY(e, index) > screenY / 2;
                    if(mVelocityTracker1 == null) {
                        // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                        mVelocityTracker1 = VelocityTracker.obtain();
                    }
                    else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker1.clear();
                    }
                    // Add a user's movement to the tracker.
                    mVelocityTracker1.addMovement(e);
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (pointerId == 0) {
                        // Remember where we started (for dragging)
                        mLastTouchX1 = MotionEventCompat.getX(e, index);
                        primaryPointer = pointerId;
                        touch1Bottom = MotionEventCompat.getY(e, index) > screenY / 2;
                        if(mVelocityTracker1 == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            mVelocityTracker1 = VelocityTracker.obtain();
                        }
                        else {
                            // Reset the velocity tracker back to its initial state.
                            mVelocityTracker1.clear();
                        }
                        // Add a user's movement to the tracker.
                        mVelocityTracker1.addMovement(e);
                    }

                    if (pointerId == 1){

                        // Remember where we started (for dragging)
                        mLastTouchX2 = MotionEventCompat.getX(e, index);
                        secondPointer = pointerId;
                        touch2Bottom = MotionEventCompat.getY(e, index) > screenY / 2;
                        if(mVelocityTracker2 == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            mVelocityTracker2 = VelocityTracker.obtain();
                        }
                        else {
                            // Reset the velocity tracker back to its initial state.
                            mVelocityTracker2.clear();
                        }
                        // Add a user's movement to the tracker.
                        mVelocityTracker2.addMovement(e);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (pointerId == 0) {
                        xVelocity1=0;
                        primaryPointer = INVALID_POINTER_ID;
                    }
                    if (pointerId == 1){
                        xVelocity2=0;
                        secondPointer = INVALID_POINTER_ID;
                    }


                    break;
                }

                case MotionEvent.ACTION_CANCEL: {

                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    if (pointerId == 0) {
                        xVelocity1=0;
                        primaryPointer = INVALID_POINTER_ID;
                    }
                    if (pointerId == 1){
                        xVelocity2=0;
                        secondPointer = INVALID_POINTER_ID;
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (primaryPointer == 0) {
                        float x = MotionEventCompat.getX(e, primaryPointer);
                        float deltaX = x - mLastTouchX1;
                        mVelocityTracker1.addMovement(e);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        mVelocityTracker1.computeCurrentVelocity(1000);
                        // Log velocity of pixels per second
                        // Best practice to use VelocityTrackerCompat where possible.
                        /*Log.d("123", "X velocity: " +
                                VelocityTrackerCompat.getXVelocity(mVelocityTracker1,
                                        pointerId));
                        Log.d("345", "Y velocity: " +
                                VelocityTrackerCompat.getYVelocity(mVelocityTracker1,
                                        pointerId));*/


                        if (touch1Bottom){
                            paddle1.update(deltaX);
                            xVelocity1 = (long) VelocityTrackerCompat.getXVelocity(mVelocityTracker1,
                                    pointerId);

                        }
                        else {
                            paddle2.update(deltaX);
                            xVelocity2 = (long) VelocityTrackerCompat.getXVelocity(mVelocityTracker1,
                                    pointerId);
                        }
                        mLastTouchX1 = x;
                    }

                    if (secondPointer == 1) {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(e, secondPointer);
                        float x = MotionEventCompat.getX(e, pointerIndex);
                        float deltaX = x - mLastTouchX2;
                        mVelocityTracker2.addMovement(e);
                        // When you want to determine the velocity, call
                        // computeCurrentVelocity(). Then call getXVelocity()
                        // and getYVelocity() to retrieve the velocity for each pointer ID.
                        mVelocityTracker2.computeCurrentVelocity(1000);
                        // Log velocity of pixels per second
                        // Best practice to use VelocityTrackerCompat where possible.
                        /*Log.d("", "X velocity: " +
                                VelocityTrackerCompat.getXVelocity(mVelocityTracker2,
                                        pointerId));
                        Log.d("", "Y velocity: " +
                                VelocityTrackerCompat.getYVelocity(mVelocityTracker2,
                                        pointerId));*/


                        if (touch2Bottom){
                            paddle1.update(deltaX);
                            xVelocity1 = (long) VelocityTrackerCompat.getXVelocity(mVelocityTracker2,
                                    pointerId);

                        }
                        else {
                            paddle2.update(deltaX);
                            xVelocity2 = (long) VelocityTrackerCompat.getXVelocity(mVelocityTracker2,
                                    pointerId);
                        }
                        mLastTouchX2 = x;


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

    //Back Button Pressed on device
    @Override
    public void onBackPressed() {
        if (localMultiplayerView.paused){
            startActivity(new Intent(LocalMultiplayer.this, MainMenu.class));
            finish();
        }
        else {
            localMultiplayerView.paused = true;
        }


    }
}


// This is the end of the BreakoutGame class

// This is the end of the BreakoutGame class


