package edu.msu.team15.connect4;

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
        updateGame();

        //decideTurn();

        TextView initial_turn = findViewById(R.id.playerText);
        initial_turn.setText(getResources().getString(R.string.playerText, getConnectFourView().getConnectFour().getCurrPlayerName()));

        /*
         * Restore any state
         */
        if (savedInstanceState != null) {
            getConnectFourView().setConnectFour((ConnectFour) savedInstanceState.getSerializable(GAME_STATE));
        }
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

                        currPlayer = xml.getAttributeValue(null, "currPlayer");
                        player1 = xml.getAttributeValue(null, "player1");
                        player2 = xml.getAttributeValue(null, "player2");

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
                final String currPlayer1 = currPlayer;
                final String player_one = player1;
                final String player_two = player2;
                view1.post(new Runnable() {

                    @Override
                    public void run() {
                        if(fail1) {
                            Toast.makeText(view1.getContext(),
                                    "Error loading game state",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            TextView player_text = findViewById(R.id.playerText);
                            player_text.setText(getResources().getString(R.string.playerText, currPlayer1));
                            getConnectFourView().getConnectFour().setPlayer1Name(player_one);
                            getConnectFourView().getConnectFour().setPlayer2Name(player_two);
                        }
                    }
                });
            }
        }).start();
    }

    public void decideTurn(){
        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String player1 = getConnectFourView().getConnectFour().getPlayer1Name();
                final Cloud cloud = new Cloud();
                //success = cloud.loadFromCloud();
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
