package one.almostd.sprong;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Controls extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);




        AdView controlsAdView = (AdView) findViewById(R.id.controlsAdView);
        AdRequest.Builder adRequestControls = new AdRequest.Builder();
        AdRequest adRequestControls1 = adRequestControls.build();
        controlsAdView.loadAd(adRequestControls1);
    }
}
