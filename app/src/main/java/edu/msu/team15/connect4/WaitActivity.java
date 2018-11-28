package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

        final Intent intent = getIntent();
        final String user = intent.getExtras().get("p1User").toString();

        final View view = findViewById(R.id.progressBar);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean success = cloud.addToGame(user);

                if (!success) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Poll for opponent
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                InputStream stream = cloud.checkForOpponent(user);

                String message = "";

                /*
                 * Create an XML parser for the result
                 */
                boolean fail = stream == null;
                if (!fail) {
                    try {
                        XmlPullParser xml2 = Xml.newPullParser();
                        xml2.setInput(stream, "UTF-8");

                        xml2.nextTag();      // Advance to first tag
                        xml2.require(XmlPullParser.START_TAG, null, "connect4");

                        String status = xml2.getAttributeValue(null, "status");
                        message = xml2.getAttributeValue(null, "message");
                        if(!status.equals("yes")) {
                            fail = true;
                        }

                        // We are done
                    } catch(XmlPullParserException ex) {
                        fail = true;
                    } catch(IOException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

                final boolean fail1 = fail;
                final String message1 = message;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        if (message1.equals("start")) {
                            timer.cancel();
                            timer.purge();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onStartGame(user);
                                }
                            });
                        }
                        else if (message1.equals("timeout")) {
                            timer.cancel();
                            timer.purge();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(),
                                            "Could not find player! ",
                                            Toast.LENGTH_SHORT).show();
                                    onCancel(view);
                                }
                            });
                        }
                    }
                });


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
