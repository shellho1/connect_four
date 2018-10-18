package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ConnectFour {

    public static final int NUM_COLUMNS = 7;
    public static final int NUM_ROWS = 6;

    /**
     * The size of the board in pixels
     */
    private int boardSize;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

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
    private GamePiece dragging = null;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    public ConnectFour(Context context) {
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

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                return onTouched(view, relX, relY);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_MOVE:
                return onMove(view, relX, relY);
        }

        return false;
    }

    private boolean onMove(View view, float x, float y) {
        // If we are dragging, move the piece and force a redraw
        if(dragging != null) {
            dragging.move(x, y);
            view.invalidate();
            return true;
        }
        return false;
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     * @param x x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onTouched(View view, float x, float y) {
        dragging = new GamePiece(view, R.drawable.spartan_green, x, y);

        view.invalidate();

        return true;
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {
        //TODO: Game piece should only be able to snap to next available location from the bottom of the grid
        
        if(dragging != null) {
            for(int col = 0; col < board.size(); col++) { //column
                for(int row = 0; row < board.get(col).size(); row++) { //row
                    if(board.get(col).get(row).hit(x, y, boardSize, scaleFactor)) {
                        dragging = null;
                        int openRow = legalMove(col);
                        if (openRow == -1) {
                            //TODO: column is full pop up message saying full
                        } else {
                            board.get(col).get(openRow).setSpaceState(view, Space.State.GREEN);
                        }
                        view.invalidate();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * function to decide if move is legal
     * @param col column to check
     * @return if legal next open space in column or -1 if illegal
     */
    private int legalMove(int col) {
        for (int row = NUM_ROWS; row > 0; row--) {
            if (board.get(col).get(row-1).getState() == Space.State.NONE) {
                return row-1;
            }
        }
        return -1;
    }
}
