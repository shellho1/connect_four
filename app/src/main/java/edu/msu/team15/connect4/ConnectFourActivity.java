package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectFourActivity extends AppCompatActivity {

    public static final String GAME_STATE = "game_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        Intent intent = getIntent();
        String player_one = intent.getStringExtra("p1");
        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(player_one);

        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            getConnectFourView().setConnectFour((ConnectFour)savedInstanceState.getSerializable(GAME_STATE));
        }
    }

    public void onSurrender(View view) {
        Intent intent = new Intent(this, WinnerScreenActivity.class);
        intent = getConnectFourView().getConnectFour().endGame(intent,
                getConnectFourView().getConnectFour().getOtherPlayer(),
                getConnectFourView().getConnectFour().getCurrPlayer());
        startActivity(intent);
        finish();
    }

    public void onDone(View view) {
        if (!getConnectFourView().getConnectFour().endTurn()) {
            Toast.makeText(view.getContext(), R.string.turn_error, Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = getIntent();
            String player_one = intent.getStringExtra("p1");
            String player_two = intent.getStringExtra("p2");
            TextView turn = findViewById(R.id.playerText);
            if (player_one == turn.getText()){
                turn.setText(player_two);
            }
            else {
                turn.setText(player_one);
            }
            turn.invalidate();
        }
    }

    public void onUndo(View view) {
        if (!getConnectFourView().doUndo()) {
            Toast.makeText(view.getContext(), R.string.undo_error, Toast.LENGTH_SHORT).show();
        }
    }

    private ConnectFourView getConnectFourView() {

        return (ConnectFourView) findViewById(R.id.connectFourView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(GAME_STATE, getConnectFourView().getConnectFour());
    }
}
