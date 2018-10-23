package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConnectFourActivity extends AppCompatActivity {

    public static final String GAME_STATE = "game_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        Intent intent = getIntent();
        String player_one = intent.getStringExtra(MainActivity.PLAYER1_NAME);
        String player_two = intent.getStringExtra(MainActivity.PLAYER2_NAME);

        getConnectFourView().getConnectFour().setPlayer1Name(player_one);
        getConnectFourView().getConnectFour().setPlayer2Name(player_two);

        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(getResources().getString(R.string.playerText, getConnectFourView().getConnectFour().getCurrPlayerName()));

        /*
         * Restore any state
         */
        if (savedInstanceState != null) {
            getConnectFourView().setConnectFour((ConnectFour) savedInstanceState.getSerializable(GAME_STATE));
        }
    }

    public void onSurrender(View view) {
        getConnectFourView().getConnectFour().endGame(
                getConnectFourView().getConnectFour().getOtherPlayer(),
                getConnectFourView().getConnectFour().getCurrPlayer());
    }

    public void onDone(View view) {
        if (!getConnectFourView().getConnectFour().endTurn()) {
            Toast.makeText(view.getContext(), R.string.turn_error, Toast.LENGTH_SHORT).show();
        } else {
            TextView turn = findViewById(R.id.playerText);
            turn.setText(getResources().getString(R.string.playerText, getConnectFourView().getConnectFour().getCurrPlayerName()));
            turn.invalidate();
        }
        getConnectFourView().invalidate();
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
