package edu.msu.team15.connect4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class GamePiece {
    /**
     * The image for the actual piece.
     */
    private Bitmap piece;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
     */
    private float x;

    /**
     * y location
     */
    private float y;

    private final float pieceScale;

    public GamePiece(View view, Space.State color, float x, float y, float gameScale) {
        this.x = x;
        this.y = y;

        this.pieceScale = gameScale;

        switch (color) {
            case NONE:
            case GREEN:
                piece = BitmapFactory.decodeResource(view.getResources(), R.drawable.spartan_green);
                break;
            case WHITE:
                piece = BitmapFactory.decodeResource(view.getResources(), R.drawable.spartan_white);
                break;

        }
    }

    /**
     * Draw the puzzle piece
     *
     * @param canvas      Canvas we are drawing on
     * @param marginX     Margin x value in pixels
     * @param marginY     Margin y value in pixels
     * @param puzzleSize  Size we draw the puzzle in pixels
     * @param scaleFactor Amount we scale the puzzle pieces when we draw them
     */
    public void draw(Canvas canvas, float marginX, float marginY,
                     int puzzleSize, float scaleFactor) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * puzzleSize, marginY + y * puzzleSize);

        // Scale it to the right size
        canvas.scale(scaleFactor * pieceScale, scaleFactor * pieceScale);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2, -piece.getHeight() / 2);

        // Draw the bitmap
        canvas.drawBitmap(piece, 0, 0, null);
        canvas.restore();
    }

    /**
     * Move the puzzle piece by dx, dy
     *
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public void move(float dx, float dy) {
        x = dx;
        y = dy;
    }
}
