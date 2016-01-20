package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Controls extends Activity {

    TextView howToPlay;
    TextView welcomeToSprong;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        howToPlay = (TextView)findViewById(R.id.HowToPlay);
        welcomeToSprong = (TextView)findViewById(R.id.WelcomeToSprong);
        next = (Button) findViewById(R.id.Next);
        howToPlay.setText(readTxt());
        howToPlay.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpHeight / 28));
        welcomeToSprong.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(dpWidth/10));
        next.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(dpWidth/9));


        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                startActivity(new Intent(Controls.this, PowerUpMenu.class));
                finish();
            }
        });

        AdView controlsAdView = (AdView) findViewById(R.id.controlsAdView);
        AdRequest.Builder adRequestControls = new AdRequest.Builder();
        AdRequest adRequestControls1 = adRequestControls.build();
        controlsAdView.loadAd(adRequestControls1);
    }
    private String readTxt(){

        InputStream inputStream = getResources().openRawResource(R.raw.howtoplay);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
}
