package com.darkbeast0106.gyroball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {
    public float x;
    public float y;
    private final int r;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BallView(Context context, float x, float y, int r, int color) {
        super(context);
        this.x = x;
        this.y = y;
        this.r = r;
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x,y,r,paint);
    }
}
