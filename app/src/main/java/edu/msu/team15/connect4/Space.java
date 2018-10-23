package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.io.Serializable;

public class Space implements Serializable {
    public enum State {
        NONE, GREEN, WHITE
    }

    private static final double COLUMN_OFFSET = .5;
    private static final int ROW_OFFSET = 1;

    /**
     * The image for the space.
     */
    private static Bitmap spaceBackground = null;

    private static Bitmap greenPiece = null;

    private static Bitmap whitePiece = null;

    /**
     * Image for gamePiece if one is in the space
     */
    transient private Bitmap gamePieceBitmap = null;

    private State state = State.NONE;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the space.
     */
    private float x;

    /**
     * y location
     */
    private float y;

    /**
     * Space's Row
     */
    private final int row;

    /**
     * Space's column
     */
    private final int column;

    private float boardScale;

    public Space(Context context, int row, int column) {
        this.row = row;
        this.column = column;

        if (spaceBackground == null) {
            spaceBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_space);
        }

        resetLocation();
    }

    /**
     * Draw the puzzle piece
     *
     * @param canvas      Canvas we are drawing on
     * @param marginX     Margin x value in pixels
     * @param marginY     Margin y value in pixels
     * @param boardSize   Size we draw the puzzle in pixels
     * @param scaleFactor Amount we scale the puzzle pieces when we draw them
     */
    public void draw(Canvas canvas, float marginX, float marginY,
                     int boardSize, float scaleFactor) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the right size
        canvas.scale(scaleFactor * boardScale, scaleFactor * boardScale);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-spaceBackground.getWidth() / 2, -spaceBackground.getHeight() / 2);

        // Draw the bitmap
        canvas.drawBitmap(spaceBackground, 0, 0, null);


        if (gamePieceBitmap != null) {
            // Draw the bitmap
            canvas.drawBitmap(gamePieceBitmap, 0, 0, null);
        }

        canvas.restore();
    }

    public int getWidth() {
        return spaceBackground.getWidth();
    }

    public int getHeight() {
        return spaceBackground.getHeight();
    }

    /**
     * Test to see if we have touched a puzzle piece
     *
     * @param testX       X location as a normalized coordinate (0 to 1)
     * @param testY       Y location as a normalized coordinate (0 to 1)
     * @param boardSize   the size of the puzzle in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int boardSize, float scaleFactor) {

        // Make relative to the location and size to the piece size
        int pX = (int) ((testX - x) * boardSize / scaleFactor) +
                getWidth() / 2;
        int pY = (int) ((testY - y) * boardSize / scaleFactor) +
                getHeight() / 2;

        if (pX < 0 || pX >= getWidth() ||
                pY < 0 || pY >= getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return (spaceBackground.getPixel(pX, pY) & 0xff000000) != 0;
    }

    public void setSpaceState(View view, State newState) {
        gamePieceBitmap = null;
        state = newState;

        switch (state) {
            case GREEN:
                if (greenPiece == null) {
                    greenPiece = BitmapFactory.decodeResource(view.getResources(), R.drawable.spartan_green);
                }
                gamePieceBitmap = greenPiece;
                break;
            case WHITE:
                if (whitePiece == null) {
                    whitePiece = BitmapFactory.decodeResource(view.getResources(), R.drawable.spartan_white);
                }
                gamePieceBitmap = whitePiece;
                break;
            case NONE:
                gamePieceBitmap = null;
                break;
        }
    }

    public State getState() {
        return state;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void resetLocation() {
        this.x = (float) ((column + COLUMN_OFFSET) * getWidth()) / (getWidth() * ConnectFour.NUM_COLUMNS);
        this.y = (float) ((row + ROW_OFFSET) * getHeight()) / (getWidth() * ConnectFour.NUM_COLUMNS);
        this.boardScale = 1;
    }

    public void setBoardScale(float boardScale) {
        this.boardScale *= boardScale;
    }
}
