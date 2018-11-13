package edu.msu.team15.connect4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String PLAYER1_NAME = "p1";
    public static final String PLAYER2_NAME = "p2";
    private static final String REMEMBER_CHECKED = "checked";
    private static final String USER_PREF = "userid";
    private static final String PASS_PREF = "password";

    private SharedPreferences settings = null;

    private boolean rememberChecked = false;

    private CheckBox rememberCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rememberCheck = findViewById(R.id.rememberCheckBox);
        EditText userField = findViewById(R.id.userEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);


        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            rememberChecked = savedInstanceState.getBoolean(REMEMBER_CHECKED);
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        userField.setText(settings.getString(USER_PREF, ""));
        passwordField.setText(settings.getString(PASS_PREF, ""));
        rememberChecked = settings.getBoolean(REMEMBER_CHECKED, false);

        updateUI();
    }

    /**
     * Handle the remember checkbox change
     * @param view view
     */
    public void onRememberClicked(View view) {
        CheckBox cb = findViewById(R.id.rememberCheckBox);
        rememberChecked = cb.isChecked();
    }

    public void onStart(View view) {
        EditText usernameEditText = findViewById(R.id.userEditText);
        String username = usernameEditText.getText().toString();

        EditText passwordEditText = findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();

        if (rememberChecked) {
            writePreferences(username, password);
        } else {
            clearPreferences();
        }

        login(username, password, view);
    }

    private void login(final String username, final String password, View view) {

        final View view1 = view;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                InputStream stream = cloud.loginUser(username,password);

                // Test for an error
                boolean fail = stream == null;
                if(!fail) {
                    try {

                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");

                        xml.nextTag();      // Advance to first tag
                        xml.require(XmlPullParser.START_TAG, null, "connect4");
                        String status = xml.getAttributeValue(null, "status");
                        if(!status.equals("yes")) {
                            fail = true;
                        }

                    } catch(IOException ex) {
                        fail = true;
                    } catch(XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch(IOException ex) {
                        }
                    }
                }

                final boolean fail1 = fail;
                view1.post(new Runnable() {

                    @Override
                    public void run() {
                        if(fail1) {
                            Toast.makeText(view1.getContext(),
                                    R.string.login_user_error,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(view1.getContext(), WaitActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).start();
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

    private void writePreferences(String username, String password) {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(USER_PREF, username);
        editor.putString(PASS_PREF, password);
        editor.putBoolean(REMEMBER_CHECKED, true);

        editor.apply();
    }

    private void clearPreferences() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.clear();

        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(REMEMBER_CHECKED, rememberChecked);
    }

    /**
     * Ensure the user interface components match the current state
     */
    private void updateUI() {
        rememberCheck.setChecked(rememberChecked);
    }
}
