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
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class LocalMultiplayer extends Activity {
    /*Holds logic of the local multiplayer - like a motherboard of computer
    Will also respond to screen touches
    */
    LocalMultiplayerView localMultiplayerView;


    @Override
    //Runs once created - start forward
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instace of inner class, where game logic is held
        localMultiplayerView = new LocalMultiplayerView(this);
        //Create a layout - I can set a static background to the layout and draw the game overtop
        FrameLayout game = new FrameLayout(this);
        game.setBackgroundResource(R.drawable.deepfield16x9);
        game.addView(localMultiplayerView);

        setContentView(game);
        //Makes the game fullscreen, by default there is usually a actionbar across the top
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }


    // Implements runnable so I have a thread and can override the run method
    class LocalMultiplayerView extends SurfaceView implements Runnable {
        //This is the game thread
        Thread gameThread = null;

        //Need this to use canvas and paint
        SurfaceHolder ourHolder;

        //Boolean to set and unset when game is running
        volatile boolean playing;

        //Game is paused at the start
        boolean paused = true;

        //Canvas is what I draw to, paint controls how I draw
        Canvas canvas;
        Paint paint;

        //Tracks the game's frame rate
        long fps;

        //This is used to help caculate the fps
        private long timeThisFrame;

        //Will keep dimensions of screen in pixels
        public int screenX;
        public int screenY;

        //I have borders for the game so the user has room to place finger and still see paddle
        int gameTop;
        int gameBottom;

        //Used in touch listener
        private float mLastTouchX1;
        private float mLastTouchX2;
        VelocityTracker mVelocityTracker1 = null;
        VelocityTracker mVelocityTracker2 = null;
        long xVelocity1;
        long xVelocity2;

        // The players paddles
        Paddle paddle1;
        Paddle paddle2;

        //Bitmaps are images
        Bitmap multiBall;
        Bitmap smallPaddle;
        Bitmap largePaddle;
        Bitmap reversePaddle;
        Bitmap bullet;

        // Up to 72 bricks
        Brick[] bricks = new Brick[72];
        int numBricks;
        int bricksInvisible;
        // An array of balls, max theoretical amount of balls is 72
        Ball[] balls = new Ball[72];
        int numBalls;

        PowerUp[] powerUps = new PowerUp[72];
        int numPowerUps;
        //Five bullets are shot at a time, that is why max amount is 72*5
        Bullet[] bullets = new Bullet[72*5];
        int numBullets;

        // The scores
        public int score1;
        public int score2;

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);//Used to save variables

        // When the we initializer
        public LocalMultiplayerView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up the object.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();
            this.setZOrderOnTop(true); //surfaceview is usually at the back, need to bring it to the front to see background
            ourHolder.setFormat(PixelFormat.TRANSPARENT);//make it transparent in order to see background behind

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;

            //Set screen borders
            gameTop = screenY / 10;
            gameBottom = screenY - screenY/10;

            //Paddles
            paddle1 = new Paddle(screenX, screenY, gameBottom, true);
            paddle2 = new Paddle(screenX, screenY, gameTop, false);

            imageDownloader();
            createBricksAndRestart();

        }

        public void imageDownloader() {
            //recieves my images from resource file drawable
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
            double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
            double screenInches = Math.sqrt(x + y);
            if (screenInches < 7) {
                multiBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.powerup);
                smallPaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.smallpaddle);
                largePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.largepaddle);
                reversePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.reverse);
                bullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet);
            }
            else {
                multiBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.poweruplarge);
                smallPaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.smallpaddlelarge);
                largePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.largepaddlelarge);
                reversePaddle = BitmapFactory.decodeResource(this.getResources(), R.drawable.reverselarge);
                bullet = BitmapFactory.decodeResource(this.getResources(), R.drawable.bulletlarge);
            }


        }


        public void createBricksAndRestart() { //Runs once instance of this class is created

            balls[0] = new Ball(screenX, screenY, 1);
            balls[1] = new Ball (screenX, screenY, 2);
            numBalls += 2;

            //Brick height and width are ratios of the screen, this is common as android devices vary in size.
            int brickWidth = screenX / 10;
            int brickHeight = screenY / 25;

            // Build a wall of bricks
            numBricks = 0;
            for (int column = 2; column < 8; column++) { //Would be a max of 10 colums and 25
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
                // Update the frame
                if (!paused) {
                    update();
                }
                // Draw the frame
                draw();


                // Calculate the fps this frame
                // I use then use the result to
                // time animations, etc.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }


            }

        }

        public void update() {
            //Always update the balls
            for (int i = 0; i < numBalls; i++) {
                balls[i].update(fps);
            }

            // Check for ball collisons, call individual update methods
            ballCollision();
            brickCollision();
            powerUpUpdate();
            bulletsUpdate();

            if (numBricks==bricksInvisible){
                endGame();
            }

        }

        public void brickCollision(){
            for (int i = 0; i < numBricks; i++) {
                brickLoop:
                if (bricks[i].getVisibility()) {
                    for (int k = 0; k < numBalls; k++) {
                        Ball ball = balls[k];//this just makes it easier for me to type, lazy I suppose
                        Point top = new Point((int) ball.getRect().centerX(), (int) ball.getRect().top);//create 4 points on ball to check for collision
                        Point bottom = new Point((int) ball.getRect().centerX(), (int) ball.getRect().bottom);
                        Point right = new Point((int) ball.getRect().right, (int) ball.getRect().centerY());
                        Point left = new Point((int) ball.getRect().left, (int) ball.getRect().centerY());

                        if (bricks[i].getRect().contains(top.x, top.y) ||
                                bricks[i].getRect().contains(bottom.x, bottom.y)) {//Ball hits top or bottom
                            ball.reverseYVelocity();
                            bricks[i].setInvisible();
                            switch (ball.lastPaddleHit) {
                                case 1:
                                    score1 += 10;
                                    break;
                                case 2:
                                    score2 += 10;
                                    break;
                            }
                            bricksInvisible += 1;
                            if (bricks[i].getRandommColour() == 0) {//The brick is yelow & yellow bricks are powerups
                                powerUps[numPowerUps] = new PowerUp(screenY,bricks[i].getRect().left,
                                        bricks[i].getRect().top, ball.lastPaddleHit, multiBall,
                                        smallPaddle, largePaddle, reversePaddle, bullet);
                                numPowerUps += 1;
                            }

                            break brickLoop;
                        }

                        if (bricks[i].getRect().contains(right.x, right.y) || //Hits left or right or paddle
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
                                        bricks[i].getRect().top, ball.lastPaddleHit, multiBall,
                                        smallPaddle, largePaddle, reversePaddle, bullet);

                                numPowerUps += 1;
                            }
                            break brickLoop;
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
                    // Check for ball colliding with paddle 1
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
                        score2 += 20;
                        if (k > 1) {
                            ball.isVisible = false;
                        }

                    }

                    // Bounce the ball back when it hits the top of screen
                    if (ball.getRect().top < gameTop &&
                            !RectF.intersects(ball.getRect(), paddle2.getRect())) {
                        ball.reset(screenX, screenY, false);
                        score1 += 20;
                        if (k > 1) {
                            ball.isVisible = false;

                        }
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
                if (powerUp.isVisible()) {
                    powerUp.update(fps);
                    //paddle 1 hit power up, etc..
                    if (paddle1.getRect().contains(powerUp.getCenterX(), powerUp.getBottomY())) {
                        powerUp.invisible();
                        powerUp.activate(System.currentTimeMillis());
                    }
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
                if (powerUp.isActive) {
                    activatePowerUp(powerUp);
                }
            }
        }
        public void activatePowerUp(PowerUp powerUp){
            long startTime = powerUp.getActivateTime();
            switch (powerUp.getPowerUpNum()) {
                case 0: { //multi-ball power up
                    balls[numBalls] = new Ball(screenX, screenY, powerUp.paddle);
                    numBalls++;
                    powerUp.unactivate();
                    powerUp.invisible();
                    break;

                }
                case 1: { //small paddle
                    if (powerUp.paddle == 1) {
                        paddle1.setPaddleShrink();
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle1.paddleShrinkReset();
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    }
                    else {
                        paddle2.setPaddleShrink();
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle2.paddleShrinkReset();
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    }
                    break;
                }
                case 2: { //large paddle
                    if (powerUp.paddle == 1) {
                        paddle1.setPaddleGrow();
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle1.setPaddleGrowReset();
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    } else {
                        paddle2.setPaddleGrow();
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle2.setPaddleGrowReset();
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    }
                    break;
                }
                case 3: { //reverse paddle movement
                    if (powerUp.paddle == 1) {
                        paddle1.reversePaddle = true;
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle1.reversePaddle = false;
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    }
                    else {
                        paddle2.reversePaddle = true;
                        if (System.currentTimeMillis() - startTime >= 5000) {
                            paddle2.reversePaddle = false;
                            powerUp.unactivate();
                            powerUp.invisible();
                        }
                    }
                    break;
                }

                case 4: { //bullet power up
                    float x = powerUp.getX();
                    float y = powerUp.getY();
                    if (powerUp.paddle == 1){
                        for (int i = 0; i < 5 ; i++){
                            bullets[numBullets] = new Bullet(screenY, x, y, i, startTime);
                            numBullets+=1;
                        }
                    }
                    else{
                        for (int i = 0; i < 5 ; i++) {
                            bullets[numBullets] = new Bullet(screenY, x, y, i, startTime);
                            numBullets += 1;
                        }
                    }
                    powerUp.unactivate();
                    powerUp.invisible();
                    break;
                }
            }

        }

        public void bulletsUpdate(){
            for (int i = 0; i<numBullets; i++) {
                if (bullets[i].isActive()) {
                    bullets[i].update(fps);
                    //bullet hits a brick
                    for (int t = 0; t < numBricks; t++) {
                        if (bricks[t].getVisibility()) {
                            if (bricks[t].getRect().contains(bullets[i].getX(), bullets[i].getY())) {
                                bricks[t].setInvisible();
                                bullets[i].unactive();
                                bricksInvisible += 1;
                                if (bullets[i].up) {
                                    score1 += 10;
                                } else {
                                    score2 += 10;
                                }
                            }
                        }
                    }
                    if(bullets[i].getY() > gameBottom || bullets[i].getY() < gameTop){
                        bullets[i].unactive();
                    }
                }
            }

        }


        // Draw the newly updated scene
        public void draw() {

            // Make sure the drawing surface is valid or it crashes

            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);//transparent background
                paint.setColor(Color.BLUE);
                // Draw the paddle
                canvas.drawRect(paddle1.getRect(), paint);
                //Draw paddle 2
                canvas.drawRect(paddle2.getRect(), paint);
                paint.setColor(Color.WHITE);
                //draw lines for borders
                canvas.drawLine(0, gameBottom, screenX, gameBottom, paint);
                canvas.drawLine(0, gameTop, screenX, gameTop, paint);
                // Draw the balls
                drawBalls(canvas);
                // Draw the bricks if visible
                drawBricks(canvas, paint);
                //Draw the powerups if visible
                drawPowerUps(canvas);
                //Draw bullets if active
                drawBullets(canvas);
                if (paused){//Right paused if paused
                    Paint textPaint = new Paint();
                    textPaint.setTextSize(100);
                    textPaint.setColor(Color.WHITE);
                    int xPos = (int)(canvas.getWidth() / 14.4);
                    int yPos = screenY/2 + (3* screenX / 25) ;
                    canvas.rotate(270, xPos, yPos);//rotate canvas
                    canvas.drawText("Paused", xPos, yPos, textPaint);
                    canvas.rotate(90, xPos, yPos);//re rotate
                }
                //Draw score
                paint.setTextSize(canvas.getHeight()/20);
                canvas.drawText(Integer.toString(score1), 10, screenY - 10, paint);
                canvas.rotate(180, screenX - 10, 10);
                canvas.drawText(Integer.toString(score2), screenX - 10, 10, paint);

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void drawBalls(Canvas canvas){
            for (int k = 0; k < numBalls; k++) {
                Ball ball = balls[k];
                if (ball.isVisible) {
                    canvas.drawOval(ball.getRect(), paint);
                }
            }
        }

        @SuppressWarnings("deprecation")
        public void drawBricks(Canvas canvas, Paint paint){
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    int num = bricks[i].getRandommColour();
                    if (num == 1 || num == 10){
                        paint.setColor(Color.BLUE);
                    }
                    else if (num == 2 || num == 11){
                        paint.setColor(Color.GREEN);
                    }
                    else if (num == 3 || num == 12){
                        paint.setColor(Color.MAGENTA);
                    }
                    else if(num == 4 || num == 13){
                        paint.setColor(Color.RED);
                    }
                    else if (num == 5 ||num == 14){
                        paint.setColor(getResources().getColor(R.color.orange));
                    }
                    else if (num == 6 || num == 15){
                        paint.setColor(getResources().getColor(R.color.dark_green));
                    }
                    else if (num == 7 || num == 16){
                        paint.setColor(getResources().getColor(R.color.light_blue));
                    }
                    else if (num == 8 || num == 17){
                        paint.setColor(getResources().getColor(R.color.purple));
                    }
                    else if (num == 9 || num == 18){
                        paint.setColor(getResources().getColor(R.color.dark_pink));
                    }
                    else{
                        paint.setColor(Color.YELLOW);
                    }
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }
        }

        public void drawPowerUps(Canvas canvas){
            for (int i = 0; i<numPowerUps; i++){
                PowerUp powerUp = powerUps[i];
                if(powerUp.isVisible()) {
                    canvas.drawBitmap(powerUp.getPowerUp(), powerUp.getX(), powerUp.getY(), paint);
                }
            }
        }

        public void drawBullets(Canvas canvas){
            for (int i = 0; i<numBullets; i++){
                if (bullets[i].isActive()) {
                    canvas.drawBitmap(bullet, bullets[i].getX(),
                            bullets[i].getY(), paint);
                }
            }
        }

        public void endGame(){
            paused=true;
            SharedPreferences.Editor e = myPrefs.edit();
            e.putInt("score1", score1); // add or overwrite score1
            e.putInt("score2", score2); // add or overwrite score2
            e.putLong("start", System.currentTimeMillis());
            e.putBoolean("singleplayer", false);
            e.apply(); // this saves to disk
            startActivity(new Intent(LocalMultiplayer.this, LmGameOver.class));//changes activity
            finish();
        }

        // Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            paused = true;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                pause();
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


