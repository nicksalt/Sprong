package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
        int score1 = myPrefs.getInt("score1", 0);
        int score2 = myPrefs.getInt("score2", 0);

        ScoreOne.setText(Integer.toString(score1));
        ScoreTwo.setText(Integer.toString(score2));
        final Button button = (Button) findViewById(R.id.NewGame);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(LmGameOver.this, LocalMultiplayer.class));
            }
        });
        if (score1 > score2){
            WinnerOne.setText("WINNER!");
        }
        if (score2<score1){
            WinnerTwo.setText("WINNER!");
        }
        if (score1==score2){
            WinnerOne.setText("TIE");
            WinnerTwo.setText("TIE");

        }
    }

}
