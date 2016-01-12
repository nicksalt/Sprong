package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_main_menu);

        final Button playButton = (Button) findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(MainMenu.this, LocalMultiplayer.class));
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
    }


}

