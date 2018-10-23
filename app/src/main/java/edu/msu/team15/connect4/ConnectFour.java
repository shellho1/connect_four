package edu.msu.team15.connect4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;

public class ConnectFour implements Serializable {

    public static final int NUM_COLUMNS = 7;
    public static final int NUM_ROWS = 6;
    public static final String WINNER_NAME = "winner_name";
    public static final String LOSER_NAME = "loser_name";

    /**
     * The size of the board in pixels
     */
    private int boardSize;

    /**
     * Left margin in pixels
     */
    private float marginX;

    /**
     * Top margin in pixels
     */
    private float marginY;

    /**
     * How much we scale the spaces
     */
    private float scaleFactor;

    /**
     * Collection of spaces
     */
    private ArrayList<ArrayList<Space>> board = new ArrayList<>();


    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    transient private GamePiece dragging = null;

    /**
     * Our player information for player 1
     */
    private Player player1 = new Player(Space.State.GREEN);

    /**
     * Our player information for player 2
     */
    private Player player2 = new Player(Space.State.WHITE);;

    private int currPlayer = 1;

    /**
     * holds the column that was played or -1 if nothing has been played yet
     */
    private int played = -1;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * number in a row to win. default 4
     * (could add constructor to change if desired)
     */
    private int winSize = 4;

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();

    transient private Context context;

    private float gameScale = 1;


    public ConnectFour(Context context) {
        this.context = context;
        for (int i = 0; i < NUM_COLUMNS; i++) {
            ArrayList<Space> column = new ArrayList<>();
            for (int j = 0; j < NUM_ROWS; j++) {
                column.add(new Space(context, j, i));
            }
            board.add(column);
        }
    }

    public void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        boardSize = wid < hit ? wid : hit;

        // Compute the margins so we center the board
        marginX = (wid - boardSize) / 2;
        marginY = (hit - boardSize) / 2;

        scaleFactor = (float) boardSize / (board.get(0).get(0).getWidth() * NUM_COLUMNS);

        for (ArrayList<Space> column : board) {
            for (Space space : column) {
                space.draw(canvas, marginX, marginY, boardSize, scaleFactor);
            }
        }

        if (dragging != null) {
            dragging.draw(canvas, marginX, marginY, boardSize, scaleFactor);
        }
    }

    /**
     * Handle a touch event from the view.
     *
     * @param view  The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        //
        // Convert an x,y location to a relative location in the
        // board.
        //
        float relX = (event.getX() - marginX) / boardSize;
        float relY = (event.getY() - marginY) / boardSize;

        int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                view.invalidate();
                touch1.copyToLast();
                return onTouched(view, relX, relY);

            case MotionEvent.ACTION_POINTER_DOWN:
                if (touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    view.invalidate();
                    touch2.copyToLast();
                    dragging = null;
                    return true;
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                view.invalidate();
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_POINTER_UP:
                if (id == touch2.id) {
                    touch2.id = -1;
                }
                else if (id == touch1.id) {
                    // Make what was touch2 now be touch 1 by swapping objects
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                view.invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                return onMove(view, relX, relY);
        }

        return false;
    }

    private void getPositions(MotionEvent event){
        for (int i = 0; i < event.getPointerCount(); i++){
            // Get the pointer id
            int id = event.getPointerId(i);

            //Convert the image coords
            float relX = (event.getX(i) - marginX) / boardSize;
            float relY = (event.getY(i) - marginY) / boardSize;

            if (id == touch1.id) {
                touch1.copyToLast();;
                touch1.x = relX;
                touch2.y = relY;
            }
            else if (id == touch2.id) {
                touch2.copyToLast();
                touch2.x = relX;
                touch1.y = relY;
            }
        }

    }
    private boolean onMove(View view, float x, float y) {
        // If we are dragging, move the piece and force a redraw

        if (touch1.id < 0) {
            return false;
        }

        if (touch2.id >= 0) {
            /*
             * Scaling
             */
            Log.i("values:",Float.toString(touch1.x) + " " + Float.toString(touch1.y) + " " + Float.toString(touch2.x) + " " + Float.toString(touch2.y));
            float d1 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            float d2 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float moveScale = d1/d2;
            scale(moveScale, touch1.x, touch1.y);
            view.invalidate();
            return true;
        }
        if (dragging != null && touch1.id >= 0) {
            dragging.move(x, y);
            view.invalidate();
            return true;
        }
        return false;
    }

    public void scale(float moveScale, float x1, float y1) {
        gameScale *= moveScale;

        for (ArrayList<Space> col : board) {
            for (Space space : col) {
                space.setBoardScale(moveScale);

                float xp = (space.getX()-x1) * moveScale + x1;
                float yp = (space.getY()-y1) * moveScale + y1;

                space.setX(xp);
                space.setY(yp);
            }
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     *
     * @param x x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onTouched(View view, float x, float y) {
        dragging = new GamePiece(view, getCurrPlayer().color, x, y, gameScale);
        view.invalidate();
        return true;
    }

    /**
     * Handle a release of a touch message.
     *
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {
        if (dragging != null) {
            for (int col = 0; col < board.size(); col++) { //column
                for (int row = 0; row < board.get(col).size(); row++) { //row
                    if (board.get(col).get(row).hit(x, y, boardSize, scaleFactor)) {
                        dragging = null;
                        int openRow = legalMove(col);
                        if (openRow == -1) {
                            Toast.makeText(view.getContext(), R.string.column_full, Toast.LENGTH_SHORT).show();
                        } else if (openRow == -2) {
                            Toast.makeText(view.getContext(), R.string.played_error, Toast.LENGTH_SHORT).show();
                        } else {
                            board.get(col).get(openRow).setSpaceState(view, getCurrPlayer().color);
                            played = col;
                        }

                        if (isWin()) {
                            endGame(getCurrPlayer(), getOtherPlayer());
                        }
                        if (isTie()) {
                            endTieGame();
                        }

                        view.invalidate();
                        return true;
                    }
                }
            }
        }
        dragging = null;
        view.invalidate();

        return false;
    }

    /**
     * function to decide if move is legal
     *
     * @param col column to check
     * @return if legal next open space in column or -1 if illegal
     */
    private int legalMove(int col) {
        if (played == -1) {
            for (int row = NUM_ROWS - 1; row >= 0; row--) {
                if (board.get(col).get(row).getState() == Space.State.NONE) {
                    return row;
                }
            }
        } else {
            return -2;
        }
        return -1;
    }

    /**
     * perform and undo on the board if possible
     * @param view
     * @return if the undo was successful
     */

    public boolean undo(View view) {
        if (played != -1) {
            for (int row = 0; row < NUM_ROWS; row++) {
                if (board.get(played).get(row).getState() != Space.State.NONE) {
                    board.get(played).get(row).setSpaceState(view, Space.State.NONE);
                    played = -1;
                    view.invalidate();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check if the board is in a winning state
     * @return whether the last play produced a win
     */
    private boolean isWin() {
        for (int i = 0; i < NUM_COLUMNS; i++) {
            for (int j = 0; j < NUM_ROWS-winSize+1; j++) {
                boolean win = true;
                for (int x = 0; x < winSize; x++) {
                    if (board.get(i).get(j+x).getState() != getCurrPlayer().color) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        for (int i = 0; i < NUM_COLUMNS-winSize+1; i++) {
            for (int j = 0; j < NUM_ROWS; j++) {
                boolean win = true;
                for (int x = 0; x < winSize; x++) {
                    if (board.get(i+x).get(j).getState() != getCurrPlayer().color) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        for (int i = 0; i < NUM_COLUMNS-winSize+1; i++) {
            for (int j = winSize-1; j < NUM_ROWS; j++) {
                boolean win = true;
                for (int x = 0; x < winSize; x++) {
                    if (board.get(i+x).get(j-x).getState() != getCurrPlayer().color) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        for (int i = winSize-1; i < NUM_COLUMNS; i++) {
            for (int j = winSize-1; j < NUM_ROWS; j++) {
                boolean win = true;
                for (int x = 0; x < winSize; x++) {
                    if (board.get(i-x).get(j-x).getState() != getCurrPlayer().color) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isTie() {
        for (ArrayList<Space> col : board) {
            for (Space space : col) {
                if (space.getState() == Space.State.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * try to end the player's turn and switch to other player
     *
     * @return if the end turn succeeded
     */
    public boolean endTurn() {
        if (played != -1) {
            currPlayer = currPlayer == 1 ? 2 : 1;
            played = -1;
            return true;
        } else {
            return false;
        }
    }

    public void endGame(Player winner, Player loser) {
        Intent intent = new Intent(context, WinnerScreenActivity.class);
        intent.putExtra(WINNER_NAME, winner.name);
        intent.putExtra(LOSER_NAME, loser.name);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public void endTieGame() {
        Intent intent = new Intent(context, TieScreenActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public Player getCurrPlayer() {
        return currPlayer == 1 ? player1 : player2;
    }

    public Player getOtherPlayer() {
        return currPlayer != 1 ? player1 : player2;
    }

    public String getCurrPlayerName() {
        return currPlayer == 1 ? player1.name : player2.name;
    }

    public void setPlayer1Name(String name) {
        player1.name = name;
    }

    public void setPlayer2Name(String name) {
        player2.name = name;
    }

    private class Player implements Serializable {
        public String name = null;

        public Space.State color;

        public Player(Space.State color) {
            this.color = color;
        }
    }

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch implements Serializable {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;

        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
    }
}
