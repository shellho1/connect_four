package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ConnectFourActivity extends AppCompatActivity{

    private String player_one;
    private String player_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        Intent intent = getIntent();
        player_one = intent.getStringExtra("p1");
        player_two = intent.getStringExtra("p2");
        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(player_one);
    }

    public void onSurrender(View view) {
        Intent intent = new Intent(this, WinnerScreenActivity.class);
        intent = getConnectFourView().getConnectFour().endGame(intent,
                getConnectFourView().getConnectFour().getOtherPlayer(),
                getConnectFourView().getConnectFour().getCurrPlayer());
        intent.putExtra("p1",player_one);
        intent.putExtra("p2",player_two);
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

    public ArrayList<String> getNames(){
        ArrayList<String> names = new ArrayList<String>();
        Intent intent = getIntent();
        String player_one = intent.getStringExtra("p1");
        String player_two = intent.getStringExtra("p2");
        names.add(player_one);
        names.add(player_two);
        return names;
    }
}
