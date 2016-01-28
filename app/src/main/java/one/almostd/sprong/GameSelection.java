package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameSelection extends Activity {
    SharedPreferences myPrefs;
    Button onePlayer;
    Button twoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        resetValues();
        initiateWidget();
        //Opens difficulty screen, otherwise starts Lmmultiplayer.
        onePlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(GameSelection.this, DifficultySelection.class));
            }
        });
        twoPlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameSelection.this, LocalMultiplayer.class));
            }
        });
    }

    public void initiateWidget(){
        onePlayer = (Button) findViewById(R.id.OnePlayer);
        twoPlayer = (Button) findViewById(R.id.TwoPlayer);

    }
    //Reset all values if they get to this screen. Have this as a backup incase the game crashes, etc.
    public void resetValues(){
        SharedPreferences.Editor e = myPrefs.edit();
        e.putInt("roundsWon1", 0);
        e.putInt("roundsWon2", 0);
        e.putInt("numRounds", 1);
        e.apply();

    }
}
