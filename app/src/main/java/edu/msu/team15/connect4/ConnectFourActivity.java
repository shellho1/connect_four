package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ConnectFourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);
    }

    public void onSurrender(View view) {
        Intent intent = new Intent(this, WinnerScreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void onDone(View view) {
        if (!getConnectFourView().getConnectFour().endTurn()) {
            //TODO: pop up dialog that turn is not ended cause they didnt play
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
