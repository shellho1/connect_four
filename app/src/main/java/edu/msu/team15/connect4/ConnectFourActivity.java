package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ConnectFourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        Intent intent = getIntent();
        String player_one = intent.getStringExtra("p1");
        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(player_one);
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
            //TODO: pop up dialog that turn is not ended cause they didnt play
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
            //TODO: Pop up dialog that undo did not work
        }
    }

    private ConnectFourView getConnectFourView() {
        return (ConnectFourView) findViewById(R.id.connectFourView);
    }
}
