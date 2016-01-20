package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameSelection extends Activity {
    SharedPreferences myPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor e = myPrefs.edit();
        e.putInt("roundsWon1", 0);
        e.putInt("roundsWon2", 0);
        e.putInt("numRounds", 1);
        e.apply();
        final Button onePlayer = (Button) findViewById(R.id.OnePlayer);
        onePlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(GameSelection.this, DifficultySelection.class));
            }
        });

        final Button twoPlayer = (Button) findViewById(R.id.TwoPlayer);
        twoPlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(GameSelection.this, LocalMultiplayer.class));
            }
        });
    }
}
