package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ConnectFourActivity extends AppCompatActivity {

    private static final String GAME_STATE = "game_state";

    private boolean myTurn = false;

    private volatile boolean success;

    private volatile int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        //TODO: Player 1 and 2 should correlate with the respective player on the server to avoid confusion
        Intent intent = getIntent();
        String player_one = intent.getStringExtra("PLAYER1_NAME");
        String player_two = intent.getStringExtra("PLAYER2_NAME");

        getConnectFourView().getConnectFour().setPlayer1Name(player_one);
        getConnectFourView().getConnectFour().setPlayer2Name(player_two);

        decideTurn();

        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(getResources().getString(R.string.playerText, getConnectFourView().getConnectFour().getCurrPlayerName()));

        /*
         * Restore any state
         */
        if (savedInstanceState != null) {
            getConnectFourView().setConnectFour((ConnectFour) savedInstanceState.getSerializable(GAME_STATE));
        }
    }

    public void decideTurn(){
        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String player1 = getConnectFourView().getConnectFour().getPlayer1Name();
                final Cloud cloud = new Cloud();
                success = cloud.loadFromCloud();
                if (success) {
                    // Set game state
                    setState(cloud,player1,timer);
                }
            }
        };
        timer.schedule(task,1,1000);
    }

    public void setState(Cloud cloud, String p1Name, Timer timer){
        //TODO: Need to figure out how to determine who's turn it is
        int row = cloud.getRow();
        int col = cloud.getColumn();
        if (row != -1 && col != -1){
            // A move has been made (values for row/col are not default)
            getConnectFourView().getConnectFour().setPiece(getConnectFourView(),row,col);
        }
        else {
            timer.cancel();
            timer.purge();
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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Cloud cloud = new Cloud();
                    cloud.saveToCloud(getConnectFourView().getConnectFour().getCurrPlayerName(),
                            getConnectFourView().getConnectFour().getMyColumn(),
                            getConnectFourView().getConnectFour().getMyRow());
                }
            }).start();

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
