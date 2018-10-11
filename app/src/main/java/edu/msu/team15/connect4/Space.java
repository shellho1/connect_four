package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Space {
    private static final double COLUMN_OFFSET = .5;
    private static final int ROW_OFFSET = 1;
    /**
     * The image for the actual piece.
     */
    private Bitmap spaceBackground;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the space.
     */
    private float x = 0;

    /**
     * y location
     */
    private float y = 0;

    /**
     * Space's Row
     */
    private int row;

    /**
     * Space's column
     */
    private int column;

    public Space(Context context, int row, int column) {
        this.row = row;
        this.column = column;

        spaceBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_space);

        this.x = (float) ((column + COLUMN_OFFSET) * getWidth()) / (getWidth() * ConnectFour.NUM_COLUMNS);
        this.y = (float) ((row + ROW_OFFSET) * getHeight()) / (getWidth() * ConnectFour.NUM_COLUMNS);
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
    public void draw(Canvas canvas, int marginX, int marginY,
                     int boardSize, float scaleFactor) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-spaceBackground.getWidth() / 2, -spaceBackground.getHeight() / 2);

        // Draw the bitmap
        canvas.drawBitmap(spaceBackground, 0, 0, null);
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
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param boardSize the size of the puzzle in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int boardSize, float scaleFactor) {

        // Make relative to the location and size to the piece size
        int pX = (int)((testX - x) * boardSize / scaleFactor) +
                getWidth() / 2;
        int pY = (int)((testY - y) * boardSize / scaleFactor) +
                getHeight() / 2;

        if(pX < 0 || pX >= getWidth() ||
                pY < 0 || pY >= getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return (spaceBackground.getPixel(pX, pY) & 0xff000000) != 0;
    }
}