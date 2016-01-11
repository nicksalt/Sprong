package one.almostd.sprong;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Controls extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        AdView controlsAdView = (AdView) findViewById(R.id.controlsAdView);
        AdRequest.Builder adRequestControls = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequestControls.addTestDevice("02157df28d874504");
        adRequestControls.addTestDevice("T416A3A414730");
        AdRequest adRequestControls1 = adRequestControls.build();
        controlsAdView.loadAd(adRequestControls1);
    }
}
