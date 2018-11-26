package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class WaitActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    volatile boolean success;
    private String opponent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        Button start = (Button) findViewById(R.id.StartGamebutton);
        start.setEnabled(false);

        final Intent intent = getIntent();
        final String user = intent.getExtras().get("p1User").toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean success = cloud.addToGame(user);
            }
        }).start();


        // Poll for opponent
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                success = cloud.checkForOpponent(user);
                if (success) {
                    timer.cancel();
                    timer.purge();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onStartGame(user);
                        }
                    });
                }
            }
        };
        timer.schedule(task, 1, 1000);
    }

    public void onStartGame(String user) {
        Intent intent = new Intent(this, ConnectFourActivity.class);
        intent.putExtra(USERNAME, user);
        startActivity(intent);
        finish();
    }

    public void onCancel(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                cloud.Disconnect();
            }
        }).start();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
