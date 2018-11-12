package edu.msu.team15.connect4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String PLAYER1_DEFAULT = "player1";
    private static final String PLAYER2_DEFAULT = "player2";
    public static final String PLAYER1_NAME = "p1";
    public static final String PLAYER2_NAME = "p2";
    private static final String REMEMBER_CHECKED = "checked";
    private static final String USER_PREF = "userid";
    private static final String PASS_PREF = "password";

    private SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onStart(View view) {
        EditText player1 = findViewById(R.id.userEditText);
        String userid = player1.getText().toString();

        EditText player2 = findViewById(R.id.passwordEditText);
        String password = player2.getText().toString();

        Intent intent = new Intent(this, ConnectFourActivity.class);
        intent.putExtra(PLAYER1_NAME, userid);
        intent.putExtra(PLAYER2_NAME, password);
        startActivity(intent);
        finish();
    }

    public void onInstruct(View view) {
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

    public void onCreateNewUser(View view) {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
        finish();
    }

    private void writePreferences(String userid, String password) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(USER_PREF, userid);
        editor.putString(PASS_PREF, password);

        editor.apply();
    }
}
