package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Space {
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

        this.x = (float) ((column+.5) * getSpaceBackground().getWidth()) / (getSpaceBackground().getWidth() * 7);
        this.y = (float) ((row+1) * getSpaceBackground().getHeight()) / (getSpaceBackground().getWidth() * 7);
    }

    /**
     * Draw the puzzle piece
     * @param canvas Canvas we are drawing on
     * @param marginX Margin x value in pixels
     * @param marginY Margin y value in pixels
     * @param boardSize Size we draw the puzzle in pixels
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

    public Bitmap getSpaceBackground() {
        return spaceBackground;
    }
}
