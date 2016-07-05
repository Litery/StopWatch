package pl.szon.stopwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Szymon on 05.07.2016.
 */
public class Circle extends View {

    private static final int START_ANGLE_POINT = 270;

    private Paint paint;
    private RectF rect;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);
        int strokeWidth = 40;
        int size = 680;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLUE);

        int margin = size - (size - 2 * strokeWidth);
        rect = new RectF(strokeWidth, strokeWidth, size - strokeWidth, size - strokeWidth);
        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    public void init(int size, int strokeWidth) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.BLUE);

        int margin = size - (size - 2 * strokeWidth);
        rect = new RectF(strokeWidth, strokeWidth, size - strokeWidth, size - strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}