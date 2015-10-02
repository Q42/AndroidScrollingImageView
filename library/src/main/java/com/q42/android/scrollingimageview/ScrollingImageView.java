package com.q42.android.scrollingimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by thijs on 08-06-15.
 */
public class ScrollingImageView extends View {
    private Bitmap[] bitmaps;
    private float speed;
    private int[] scene;
    private int arrayIndex = 0;
    private int maxBitmapHeight = 0;

    private Rect clipBounds = new Rect();
    private float offset = 0;

    private boolean isStarted;

    public ScrollingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ParallaxView, 0, 0);
        try {
            speed = ta.getDimension(R.styleable.ParallaxView_speed, 10);
            int sceneLength = ta.getInt(R.styleable.ParallaxView_sceneLength, 1000);
            int type = ta.peekValue(R.styleable.ParallaxView_src).type;
            if (type == TypedValue.TYPE_REFERENCE) {
                int resourceId = ta.getResourceId(R.styleable.ParallaxView_src, 0);
                TypedArray typedArray = getResources().obtainTypedArray(resourceId);
                try {
                    bitmaps = new Bitmap[typedArray.length()];
                    for (int i = 0; i < typedArray.length(); i++) {
                        bitmaps[i] = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(i, 0));
                        maxBitmapHeight = Math.max(bitmaps[i].getHeight(), maxBitmapHeight);
                    }

                    Random random = new Random();
                    this.scene = new int[sceneLength];
                    for (int i = 0; i < this.scene.length; i++) {
                        this.scene[i] = random.nextInt(bitmaps.length);
                    }
                } finally {
                    typedArray.recycle();
                }
            } else if (type == TypedValue.TYPE_STRING) {
                bitmaps = new Bitmap[]{BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.ParallaxView_src, 0))};
                scene = new int[]{0};
                maxBitmapHeight = bitmaps[0].getHeight();
            }
        } finally {
            ta.recycle();
        }
        start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxBitmapHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }

        canvas.getClipBounds(clipBounds);

        while (offset <= -getBitmap(arrayIndex).getWidth()) {
            offset += getBitmap(arrayIndex).getWidth();
            arrayIndex = (arrayIndex + 1) % scene.length;
        }

        float left = offset;
        for (int i = 0; left < clipBounds.width(); i++) {
            Bitmap bitmap = getBitmap((arrayIndex + i) % scene.length);
            int width = bitmap.getWidth();
            canvas.drawBitmap(bitmap, getBitmapLeft(width, left), 0, null);
            left += width;
        }

        if (isStarted) {
            offset -= abs(speed);
            postInvalidateOnAnimation();
        }
    }

    private Bitmap getBitmap(int sceneIndex) {
        return bitmaps[scene[sceneIndex]];
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
