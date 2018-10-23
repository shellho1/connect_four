package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WinnerScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_screen);

        String winnerName = getIntent().getStringExtra(ConnectFour.WINNER_NAME);
        String loserName = getIntent().getStringExtra(ConnectFour.LOSER_NAME);

        TextView winnerTextView = findViewById(R.id.nameWinner);
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
