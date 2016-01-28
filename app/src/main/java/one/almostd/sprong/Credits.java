package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Credits extends Activity {

    TextView credits;
    AdView creditsAd;
    ImageButton logo;
    DisplayMetrics displayMetrics;
    float dpHeight;
    float dpWidth;
    AdRequest.Builder adRequestCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        // Get a Display object to access screen details
        displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        loadAd();
        initiateWidgets();

        //Goes to our website if logo button clicked.
        logo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.almostd.one"));
                startActivity(browserIntent);
                finish();
            }
        });
    }
    //Set text and text size based on screen size. SP is a scaled font unit
    public void initiateWidgets(){
        creditsAd = (AdView) findViewById(R.id.creditsAdView);
        credits = (TextView) findViewById(R.id.CreditsText);
        credits.setText(readTxt());
        credits.setTextSize(TypedValue.COMPLEX_UNIT_SP, (int) (dpHeight / 26));
        logo = (ImageButton) findViewById(R.id.Logo);
    }

    public void loadAd(){
        creditsAd = (AdView) findViewById(R.id.creditsAdView);
        adRequestCredits = new AdRequest.Builder();
        creditsAd.loadAd(adRequestCredits.build());
    }

    private String readTxt(){
        //reads text file
        InputStream inputStream = getResources().openRawResource(R.raw.credits);
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
