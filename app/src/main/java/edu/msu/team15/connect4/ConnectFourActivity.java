package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectFourActivity extends AppCompatActivity {

    private static final String GAME_STATE = "game_state";

    private volatile boolean success;

    private volatile int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        final Intent intent = getIntent();
        final String user = intent.getExtras().get(WaitActivity.USERNAME).toString();

        getConnectFourView().getConnectFour().setUsername(user);

        updateGame();

        /*
         * Restore any state
         */
        if (savedInstanceState != null) {
            getConnectFourView().setConnectFour((ConnectFour) savedInstanceState.getSerializable(GAME_STATE));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        waitTurn();
    }

    private void updateGame() {
        final View view1 = (View) findViewById(R.id.playerText);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final InputStream stream = cloud.loadFromCloud();

                String currPlayer = "";
                String player1 = "";
                String player2 = "";
                String boardState = "";

                boolean fail = stream == null;
                if (!fail) {
                    try {

                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");

                        xml.nextTag();      // Advance to first tag
                        xml.require(XmlPullParser.START_TAG, null, "connect4");
                        String status = xml.getAttributeValue(null, "status");
                        if (!status.equals("yes")) {
                            fail = true;
                        }

                        currPlayer = xml.getAttributeValue(null, "currPlayer");
                        player1 = xml.getAttributeValue(null, "player1");
                        player2 = xml.getAttributeValue(null, "player2");
                        boardState = xml.getAttributeValue(null, "boardState");

                    } catch (IOException ex) {
                        fail = true;
                    } catch (XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

                final boolean fail1 = fail;
                final String currPlayer1 = currPlayer;
                final String player_one = player1;
                final String player_two = player2;
                final String boardState1 = boardState;
                view1.post(new Runnable() {

                    @Override
                    public void run() {
                        if (fail1) {
                            Toast.makeText(view1.getContext(),
                                    "Error loading game state",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            TextView player_text = findViewById(R.id.playerText);
                            player_text.setText(getResources().getString(R.string.playerText, currPlayer1));
                            getConnectFourView().getConnectFour().setPlayer1Name(player_one);
                            getConnectFourView().getConnectFour().setPlayer2Name(player_two);
                            getConnectFourView().getConnectFour().setCurrPlayer(currPlayer1.equals(player_one) ? 1 : 2);
                            getConnectFourView().getConnectFour().setBoard(player_text, boardState1);

                            findViewById(R.id.connectFourView).invalidate();
                        }
                    }
                });
            }
        }).start();
    }

    public void waitTurn() {
        final View view1 = (View) findViewById(R.id.playerText);

        disableUI();

        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                InputStream stream = cloud.checkTurn(getConnectFourView().getConnectFour().getUsername());

                String currPlayer = "";
                String winner = "";
                String player1 = "";
                String player2 = "";
                Long timestamp = Calendar.getInstance().getTime().getTime();

                boolean fail = stream == null;
                if (!fail) {
                    try {

                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");

                        xml.nextTag();      // Advance to first tag
                        xml.require(XmlPullParser.START_TAG, null, "connect4");
                        String status = xml.getAttributeValue(null, "status");
                        if (!status.equals("yes")) {
                            fail = true;
                        }

                        currPlayer = xml.getAttributeValue(null, "currPlayer");
                        player1 = xml.getAttributeValue(null, "player1");
                        player2 = xml.getAttributeValue(null, "player2");
                        winner = xml.getAttributeValue(null, "winner");

                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d = parser.parse(xml.getAttributeValue(null, "timestamp"));
                        timestamp = d.getTime();

                    } catch (IOException ex) {
                        fail = true;
                    } catch (XmlPullParserException ex) {
                        fail = true;
                    } catch (ParseException e) {
                        fail = true;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }

                final boolean fail1 = fail;
                final String currPlayer1 = currPlayer;
                final String player_one = player1;
                final String player_two = player2;
                final Long timestamp1 = timestamp;
                Integer winner2;
                try {
                    winner2 = Integer.parseInt(winner);
                } catch (NumberFormatException ex) {
                    winner2 = 0;
                }
                final Integer winner1 = winner2;
                view1.post(new Runnable() {

                    @Override
                    public void run() {
                        if (fail1) {
                            Toast.makeText(view1.getContext(),
                                    "Error waiting turn",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (winner1 != 0) {
                                endGame(winner1, player_one, player_two);
                                timer.cancel();
                                timer.purge();
                            }

                            Date currentTime = Calendar.getInstance().getTime();
                            if ( currentTime.getTime() - timestamp1 > 30000)
                            {
                                endGame(getConnectFourView().getConnectFour().getUsername().equals(player_one) ? 1 : 2,
                                        player_one, player_two);
                            }

                            if (currPlayer1.equals(getConnectFourView().getConnectFour().getUsername())) {
                                setState(timer);
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 1, 1000);
    }

    public void endGame(Integer winner, String player1, String player2) {
        switch (winner) {
            case 3:
                getConnectFourView().getConnectFour().endTieGame();
                break;
            case 2:
                getConnectFourView().getConnectFour().endGame(player2, player1);
                break;
            case 1:
                getConnectFourView().getConnectFour().endGame(player1, player2);
                break;
        }
    }

    public void setState(Timer timer) {
        //TODO: Need to figure out how to determine who's turn it is
        updateGame();
        enableUI();
        timer.cancel();
        timer.purge();

    }

    private void enableUI() {
        findViewById(R.id.doneButton).setEnabled(true);
        findViewById(R.id.undoButton).setEnabled(true);
        findViewById(R.id.surrenderButton).setEnabled(true);
        getConnectFourView().getConnectFour().setMyTurn(true);

        findViewById(R.id.connectFourView).invalidate();
    }

    private void disableUI() {
        findViewById(R.id.doneButton).setEnabled(false);
        findViewById(R.id.undoButton).setEnabled(false);
        findViewById(R.id.surrenderButton).setEnabled(false);
        getConnectFourView().getConnectFour().setMyTurn(false);

        findViewById(R.id.doneButton).invalidate();
    }

    public void onSurrender(View view) {
        getConnectFourView().getConnectFour().endGame(
                getConnectFourView().getConnectFour().getOtherPlayer(),
                getConnectFourView().getConnectFour().getCurrPlayer());
    }

    public void onDone(final View view) {
        final View view1 = view;

        if (!getConnectFourView().getConnectFour().endTurn()) {
            Toast.makeText(view.getContext(), R.string.turn_error, Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Cloud cloud = new Cloud();
                    success = cloud.saveToCloud(getConnectFourView().getConnectFour().getCurrPlayerInt(),
                            getConnectFourView().getConnectFour().getUsername(),
                            getConnectFourView().getConnectFour().getBoardString());

                    view1.post(new Runnable() {
                        @Override
                        public void run() {
                            updateGame();
                            waitTurn();
                        }
                    });
                }
            }).start();
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
