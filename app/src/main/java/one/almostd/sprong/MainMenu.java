package one.almostd.sprong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
public class MainMenu extends Activity {

    Display display;
    DisplayMetrics outMetrics;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_main_menu);


        final Button playButton = (Button) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, GameSelection.class));
            }
        });

        final Button controlsButton = (Button) findViewById(R.id.Controls);
        controlsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, Controls.class));
            }
        });
        AdView adViewBottom = (AdView) findViewById(R.id.menuBottomAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBottom.loadAd(adRequest);
        display = getWindowManager().getDefaultDisplay();
        outMetrics = new DisplayMetrics();
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.d("DIM", Float.toString(dpHeight) +'x'+ Float.toString(dpWidth));
    }



}

