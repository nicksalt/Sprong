package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DifficultySelection extends Activity {
    SharedPreferences myPrefs;
    SharedPreferences.Editor e;
    Button easy;
    Button medium;
    Button hard;
    Button extreme;
    int speedAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selection);
        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        easy = (Button) findViewById(R.id.Easy);
        medium = (Button) findViewById(R.id.Medium);
        hard = (Button) findViewById(R.id.Hard);
        extreme = (Button) findViewById(R.id.Extreme);
        e = myPrefs.edit();
        easy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                speedAI = 1;
                e.putInt("speedAI", speedAI);
                e.apply();
                startActivity(new Intent(DifficultySelection.this, SinglePlayer.class));
                finish();
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                speedAI = 3;
                e.putInt("speedAI", speedAI);
                e.apply();
                startActivity(new Intent(DifficultySelection.this, SinglePlayer.class));
                finish();
            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                speedAI = 5;
                e.putInt("speedAI", speedAI);
                e.apply();
                startActivity(new Intent(DifficultySelection.this, SinglePlayer.class));
                finish();
            }
        });
        extreme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                speedAI = 7;
                e.putInt("speedAI", speedAI);
                e.apply();
                startActivity(new Intent(DifficultySelection.this, SinglePlayer.class));
                finish();
            }
        });
    }
}
