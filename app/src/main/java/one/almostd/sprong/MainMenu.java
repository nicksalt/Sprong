package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
public class MainMenu extends Activity {

    AdView adViewBottom;
    AdRequest adRequest;
    Button playButton;
    Button controlsButton;
    Button creditsButton;


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_main_menu);
        loadAd();
        initiateWidgets();

        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, GameSelection.class));
            }
        });

        controlsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, Controls.class));
            }
        });

        creditsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, Credits.class));
            }
        });

    }

    public void initiateWidgets(){
        playButton = (Button) findViewById(R.id.PlayButton);
        controlsButton = (Button) findViewById(R.id.Controls);
        creditsButton = (Button) findViewById(R.id.Credits);
    }

    public void loadAd(){
        adViewBottom = (AdView) findViewById(R.id.menuBottomAdView);
        adRequest = new AdRequest.Builder().build();
        adViewBottom.loadAd(adRequest);
    }


}

