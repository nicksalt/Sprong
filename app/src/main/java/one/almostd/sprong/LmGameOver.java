package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class LmGameOver extends Activity {
    SharedPreferences myPrefs;
    int numRoundsWon1;
    int numRoundsWon2;
    int numRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lm_game_over);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        TextView ScoreOne =(TextView) findViewById(R.id.Score1);
        TextView ScoreTwo =(TextView) findViewById(R.id.Score2);
        TextView rounds1 = (TextView) findViewById(R.id.RoundsWon1);
        TextView rounds2 = (TextView) findViewById(R.id.RoundsWon2);
        ImageView crown1 = (ImageView) findViewById(R.id.Crown1);
        ImageView crown2 = (ImageView) findViewById(R.id.Crown2);
        final Button newGame = (Button) findViewById(R.id.NewGame);
        final Button nextGame = (Button) findViewById(R.id.NextGame);
        final Button mainMenu = (Button) findViewById(R.id.MainMenu);
        int score1 = myPrefs.getInt("score1", 0);
        int score2 = myPrefs.getInt("score2", 0);
        numRoundsWon1 = myPrefs.getInt("roundsWon1", 0);
        numRoundsWon2 = myPrefs.getInt("roundsWon2", 0);
        numRounds = myPrefs.getInt("numRounds", 1);

        final InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6595053800412592/7739012062");
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
                newGame.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor e = myPrefs.edit();
                        e.putInt("roundsWon1", 0);
                        e.putInt("roundsWon2", 0);
                        e.putInt("numRounds", 1);
                        e.apply();
                        // Perform action on click
                        startActivity(new Intent(LmGameOver.this, LocalMultiplayer.class));

                        finish();
                    }
                });
                mainMenu.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        SharedPreferences.Editor e = myPrefs.edit();
                        e.putInt("roundsWon1", 0);
                        e.putInt("roundsWon2", 0);
                        e.putInt("numRounds", 1);
                        e.apply();
                        startActivity(new Intent(LmGameOver.this, MainMenu.class));
                        finish();
                    }
                });
                nextGame.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences.Editor e = myPrefs.edit();
                        e.putInt("roundsWon1", numRoundsWon1);
                        e.putInt("roundsWon2", numRoundsWon2);
                        e.putInt("numRounds", numRounds+1);
                        e.apply();
                        // Perform action on click
                        startActivity(new Intent(LmGameOver.this, LocalMultiplayer.class));
                        finish();
                    }
                });
            }
        });



        ScoreOne.setText(Integer.toString(score1));
        ScoreTwo.setText(Integer.toString(score2));

        if (score1 > score2){
            numRoundsWon1+=1;
            crown1.setBackgroundResource(R.drawable.crown);
        }
        if (score2 > score1){
            numRoundsWon2+=1;
            crown2.setBackgroundResource(R.drawable.crown);
        }

        rounds1.setText(Integer.toString(numRoundsWon1) + "/" + Integer.toString(numRounds));
        rounds2.setText(Integer.toString(numRoundsWon2) + "/" + Integer.toString(numRounds));


    }

}
