package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PowerUpMenu extends Activity {

    TextView powerUpMenu;
    TextView yellowBrick;
    TextView smallPaddle;
    TextView largePaddle;
    TextView multiBall;
    TextView reverse;
    TextView bullet;
    Button mainMenu;
    DisplayMetrics displayMetrics;
    float dpWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_up_menu);
        AdView controlsAdView = (AdView) findViewById(R.id.powerUpAd);
        AdRequest.Builder adRequestControls = new AdRequest.Builder();
        AdRequest adRequestControls1 = adRequestControls.build();
        controlsAdView.loadAd(adRequestControls1);
        displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        initiateWidgets();
        mainMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                startActivity(new Intent(PowerUpMenu.this, MainMenu.class));
                finish();
            }
        });


    }

    public void initiateWidgets(){
        smallPaddle = (TextView) findViewById(R.id.SmallPaddleText);
        largePaddle = (TextView) findViewById(R.id.LargePaddleText);
        multiBall = (TextView) findViewById(R.id.MultiBallText);
        reverse = (TextView) findViewById(R.id.ReverseText);
        bullet = (TextView) findViewById(R.id.BulletText);
        smallPaddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 22));
        largePaddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 22));
        multiBall.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 22));
        reverse.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 22));
        bullet.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 22));
        powerUpMenu = (TextView) findViewById(R.id.PowerUpHome);
        powerUpMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 9));
        yellowBrick = (TextView) findViewById(R.id.YellowBrick);
        yellowBrick.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 16));
        mainMenu = (Button) findViewById(R.id.MainMenuPowerUp);
        mainMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(dpWidth/9));
        }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(PowerUpMenu.this, Controls.class));
        finish();
    }
}
