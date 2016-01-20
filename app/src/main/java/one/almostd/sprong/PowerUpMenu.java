package one.almostd.sprong;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PowerUpMenu extends Activity {

    TextView powerUpMenu;
    TextView yellowBrick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_up_menu);
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        powerUpMenu = (TextView) findViewById(R.id.PowerUpHome);
        powerUpMenu.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 9));
        yellowBrick = (TextView) findViewById(R.id.YellowBrick);
        yellowBrick.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 16));
        AdView controlsAdView = (AdView) findViewById(R.id.powerUpAd);
        AdRequest.Builder adRequestControls = new AdRequest.Builder();
        AdRequest adRequestControls1 = adRequestControls.build();
        controlsAdView.loadAd(adRequestControls1);
    }
}
