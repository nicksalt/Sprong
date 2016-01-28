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
    boolean singleplayer;
    TextView ScoreOne;
    TextView ScoreTwo;
    TextView rounds1;
    TextView rounds2;
    ImageView crown1;
    ImageView crown2;
    Button newGame;
    Button nextGame;
    Button mainMenu;
    int score1;
    int score2;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    long end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lm_game_over);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        end = System.currentTimeMillis();
        getSharedPreferences();
        initiateWidgets();
        loadAd();
        //Starts different activities based on what button is clicked
        newGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor e = myPrefs.edit();
                //Resets variables
                e.putInt("roundsWon1", 0);
                e.putInt("roundsWon2", 0);
                e.putInt("numRounds", 1);
                e.apply();
                // Perform action on click
                startActivity(new Intent(LmGameOver.this, GameSelection.class));
                finish();
            }
        });
        mainMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                SharedPreferences.Editor e = myPrefs.edit();
                //Resets variables
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
                //Ads a round if they want to play a game
                e.putInt("roundsWon1", numRoundsWon1);
                e.putInt("roundsWon2", numRoundsWon2);
                e.putInt("numRounds", numRounds + 1);
                e.apply();
                // Perform action on click
                if (singleplayer) {
                    startActivity(new Intent(LmGameOver.this, SinglePlayer.class));
                } else {
                    startActivity(new Intent(LmGameOver.this, LocalMultiplayer.class));
                }
                finish();
            }
        });

    }
    //Recieves ints I have saved in Shared Preferences
    public void getSharedPreferences(){
        singleplayer = myPrefs.getBoolean("singleplayer", true);
        score1 = myPrefs.getInt("score1", 0);
        score2 = myPrefs.getInt("score2", 0);
        numRoundsWon1 = myPrefs.getInt("roundsWon1", 0);
        numRoundsWon2 = myPrefs.getInt("roundsWon2", 0);
        numRounds = myPrefs.getInt("numRounds", 1);
    }

    public void initiateWidgets(){
        //Sets scores, pictures, etc.
        ScoreOne =(TextView) findViewById(R.id.Score1);
        ScoreTwo =(TextView) findViewById(R.id.Score2);
        rounds1 = (TextView) findViewById(R.id.RoundsWon1);
        rounds2 = (TextView) findViewById(R.id.RoundsWon2);
        crown1 = (ImageView) findViewById(R.id.Crown1);
        crown2 = (ImageView) findViewById(R.id.Crown2);
        newGame = (Button) findViewById(R.id.NewGame);
        nextGame = (Button) findViewById(R.id.NextGame);
        mainMenu = (Button) findViewById(R.id.MainMenu);
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

    public void loadAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6595053800412592/7739012062");
        adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();

            }
        });
    }

}
