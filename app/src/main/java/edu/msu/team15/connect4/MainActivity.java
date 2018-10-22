package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String PLAYER1_DEFAULT = "player1";
    public static final String PLAYER2_DEFAULT = "player2";
    public static final String PLAYER1_NAME = "p1";
    public static final String PLAYER2_NAME = "p2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onStart(View view) {
        EditText player1 = findViewById(R.id.playerOneText);
        String p1 = player1.getText().toString().equals("") ? PLAYER1_DEFAULT : player1.getText().toString();

        EditText player2 = findViewById(R.id.playerTwoText);
        String p2 = player2.getText().toString().equals("") ? PLAYER2_DEFAULT : player2.getText().toString();

        Intent intent = new Intent(this, ConnectFourActivity.class);
        intent.putExtra(PLAYER1_NAME, p1);
        intent.putExtra(PLAYER2_NAME, p2);
        startActivity(intent);
        finish();
    }

    public void onInstruct(View view){
        // User has clicked the how to play button
        // Instantiate a dialog box builder
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.instruction);
        builder.setMessage(R.string.instruct_body);
        builder.setPositiveButton(android.R.string.ok, null);

        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
