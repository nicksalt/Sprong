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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Controls extends Activity {

    TextView howToPlay;
    TextView welcomeToSprong;
    Button next;
    DisplayMetrics displayMetrics;
    float dpHeight;
    float dpWidth;
    AdView controlsAdView;
    AdRequest.Builder adRequestControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        //Use to set font size based on screen size.
        displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        loadAd();
        initiateWidgets();

        //Looks for a click on the next button
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Goes to powerup screen

                startActivity(new Intent(Controls.this, PowerUpMenu.class));
                finish();
            }
        });


    }
    //setting text and text size. SP is a font is a scaled font unit
    public void initiateWidgets(){
        howToPlay = (TextView)findViewById(R.id.HowToPlay);
        welcomeToSprong = (TextView)findViewById(R.id.WelcomeToSprong);
        next = (Button) findViewById(R.id.Next);
        howToPlay.setText(readTxt());
        howToPlay.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpHeight / 28));
        welcomeToSprong.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int)(dpWidth/10));
        next.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpWidth / 9));
    }
    // Load ads
    public void loadAd(){
        controlsAdView = (AdView) findViewById(R.id.controlsAdView);
        adRequestControls = new AdRequest.Builder();
        controlsAdView.loadAd(adRequestControls.build());
    }
    //Reads text from a .txt file.
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
