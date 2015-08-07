package com.q42.android.scrollingimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by thijs on 08-06-15.
 */
public class ScrollingImageView extends View {
    private final int speed;
    private final Bitmap bitmap;

    private Rect clipBounds = new Rect();
    private float offset = 0;

    private boolean isStarted;

    public ScrollingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ParallaxView, 0, 0);
        try {
            speed = ta.getDimensionPixelSize(R.styleable.ParallaxView_speed, 10);
            bitmap = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.ParallaxView_src, 0));
        } finally {
            ta.recycle();
        }
        start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), bitmap.getHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }

        canvas.getClipBounds(clipBounds);

        float layerWidth = bitmap.getWidth();
        if (offset < -layerWidth) {
            offset += (floor(abs(offset) / layerWidth) * layerWidth);
        }

        float left = offset;
        while (left < clipBounds.width()) {
            canvas.drawBitmap(bitmap, getBitmapLeft(layerWidth, left), 0, null);
            left += layerWidth;
        }

        if (isStarted) {
            offset -= abs(speed);
            postInvalidateOnAnimation();
        }
    }

    private float getBitmapLeft(float layerWidth, float left) {
        if (speed < 0) {
            return clipBounds.width() - layerWidth - left;
        } else {
            return left;
        }
    }

    /**
     * Start the animation
     */
    public void start() {
        if (!isStarted) {
            isStarted = true;
            postInvalidateOnAnimation();
        }
    }

    /**
     * Stop the animation
     */
    public void stop() {
        if (isStarted) {
            isStarted = false;
            invalidate();
        }
    }
}
