package one.almostd.sprong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BackgroundMessage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_message);
        TextView attention = (TextView) findViewById(R.id.Attention);
        attention.setTextColor(Color.RED);
        final Button button = (Button) findViewById(R.id.RestartButtonBackground);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(BackgroundMessage.this, MainMenu.class));
                finish();
            }
        });
    }
}
