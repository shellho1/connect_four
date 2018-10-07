package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Space {
    /**
     * The image for the actual piece.
     */
    private Bitmap nodeBackground;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
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

        nodeBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_space);
    }
}
