package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class LmGameOver extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lm_game_over);
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        TextView ScoreOne =(TextView) findViewById(R.id.Score1);
        TextView ScoreTwo =(TextView) findViewById(R.id.Score2);
        TextView WinnerOne = (TextView) findViewById(R.id.Winner1);
        TextView WinnerTwo = (TextView) findViewById(R.id.Winner2);
        final Button button = (Button) findViewById(R.id.NewGame);
        int score1 = myPrefs.getInt("score1", 0);
        int score2 = myPrefs.getInt("score2", 0);
        final InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6595053800412592/7739012062");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("02157df28d874504")
                .addTestDevice("T416A3A414730")
                .build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });



        ScoreOne.setText(Integer.toString(score1));
        ScoreTwo.setText(Integer.toString(score2));
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(LmGameOver.this, LocalMultiplayer.class));
            }
        });
        if (score1 > score2){
            WinnerOne.setText("WINNER!");
            WinnerOne.setTextColor(Color.GREEN);
            WinnerTwo.setText("");
        }
        if (score2 > score1){
            WinnerTwo.setText("WINNER!");
            WinnerTwo.setTextColor(Color.GREEN);
            WinnerOne.setText("");
        }
        if (score1 == score2){
            WinnerOne.setText("TIE");
            WinnerTwo.setText("TIE");
            WinnerOne.setTextColor(Color.GREEN);
            WinnerTwo.setTextColor(Color.GREEN);


        }

    }

}
