package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WinnerScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_screen);

        Intent intent = getIntent();
        String player_one = intent.getStringExtra("p1");
        String player_two = intent.getStringExtra("p2");

        String winnerName = getIntent().getStringExtra(ConnectFour.WINNER_NAME);
        String loserName = getIntent().getStringExtra(ConnectFour.LOSER_NAME);

        Log.i("one and two", player_one + " " + player_two);
        Log.i("winner and loser", winnerName + " " + loserName);


        // TODO: need a way to differentiate between intents from Surrender button and actually winning the game normally
        if (winnerName == "player1"){
            winnerName = player_one;
            loserName = player_two;
        }
        else {
            winnerName = player_two;
            loserName = player_one;
        }

        TextView winnerTextView = findViewById(R.id.rematch);
        winnerTextView.setText(winnerName);

        TextView loserTextView = findViewById(R.id.nameLoser);
        loserTextView.setText(loserName);
    }

    public void onRestart(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
