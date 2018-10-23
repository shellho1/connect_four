package edu.msu.team15.connect4;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * The view we will draw out hatter in
 */
public class ConnectFourView extends View {

    /**
     * The game state
     */

    private ConnectFour connectFour;

    public ConnectFourView(Context context) {
        super(context);
        init(null, 0);
    }

    public ConnectFourView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ConnectFourView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        connectFour = new ConnectFour(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        connectFour.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return connectFour.onTouchEvent(this, event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public ConnectFour getConnectFour() {
        return connectFour;
    }

    public boolean doUndo() {
        return connectFour.undo(this);
    }

    public void setConnectFour(ConnectFour connectFour) {
        this.connectFour = connectFour;
    }
}
