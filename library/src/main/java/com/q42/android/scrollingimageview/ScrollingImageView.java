package com.q42.android.scrollingimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.util.Collections.singletonList;

/**
 * Created by thijs on 08-06-15.
 */
@SuppressWarnings("FieldCanBeLocal") // Declaring fields outside of onDraw improves performance
public class ScrollingImageView extends View {
    public static ScrollingImageViewBitmapLoader BITMAP_LOADER = new ScrollingImageViewBitmapLoader() {
        @Override
        public Bitmap loadDrawable(Context context, int resourceId) {
            Drawable drawable = context.getResources().getDrawable(resourceId, context.getTheme());
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            // Render any other kind of drawable to a bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    };

    public Paint paint = null;

    private List<Bitmap> bitmaps;
    /** Pixels per second */
    private final double speed;
    private int[] scene;
    private int arrayIndex = 0;
    private int maxBitmapHeight = 0;

    private final Rect clipBounds = new Rect();
    private float offset = 0;

    /** Moment when the last call to onDraw() started */
    private static final double MICROS_PER_SECOND = 1e6;
    private Instant lastFrameInstant = null;
    private long frameTimeMicros = -1;

    private boolean isStarted;

    public ScrollingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollingImageView, 0, 0);
        int initialState = 0;
        try {
            initialState = ta.getInt(R.styleable.ScrollingImageView_initialState, 0);
            speed = ta.getDimension(R.styleable.ScrollingImageView_speed, 60);
            int sceneLength = ta.getInt(R.styleable.ScrollingImageView_sceneLength, 1000);
            final int randomnessResourceId = ta.getResourceId(R.styleable.ScrollingImageView_randomness, 0);
            // When true, randomness is ignored and bitmaps are loaded in the order as they appear in the src array */
            final boolean contiguous = ta.getBoolean(R.styleable.ScrollingImageView_contiguous, false);

            int[] randomness = new int[0];
            if (randomnessResourceId > 0) {
                randomness = getResources().getIntArray(randomnessResourceId);
            }

            int type = isInEditMode() ? TypedValue.TYPE_STRING : ta.peekValue(R.styleable.ScrollingImageView_source).type;
            if (type == TypedValue.TYPE_REFERENCE) {
                int resourceId = ta.getResourceId(R.styleable.ScrollingImageView_source, 0);
                TypedArray typedArray = getResources().obtainTypedArray(resourceId);
                try {
                    int bitmapsSize = 0;
                    for (int r : randomness) {
                        bitmapsSize += r;
                    }

                    bitmaps = new ArrayList<>(Math.max(typedArray.length(), bitmapsSize));

                    for (int i = 0; i < typedArray.length(); i++) {
                        int multiplier = 1;
                        if (randomness.length > 0 && i < randomness.length) {
                            multiplier = Math.max(1, randomness[i]);
                        }

                        Bitmap bitmap = BITMAP_LOADER.loadDrawable(getContext(), typedArray.getResourceId(i, 0));
                        for (int m = 0; m < multiplier; m++) {
                            bitmaps.add(bitmap);
                        }

                        maxBitmapHeight = Math.max(bitmap.getHeight(), maxBitmapHeight);
                    }

                    Random random = new Random();
                    this.scene = new int[sceneLength];
                    for (int i = 0; i < this.scene.length; i++) {
                        if (contiguous){
                            this.scene[i] = i % bitmaps.size();
                        } else {
                            this.scene[i] = random.nextInt(bitmaps.size());
                        }
                    }
                } finally {
                    typedArray.recycle();
                }
            } else if (type == TypedValue.TYPE_STRING) {
                final Bitmap bitmap = BITMAP_LOADER.loadDrawable(getContext(), ta.getResourceId(R.styleable.ScrollingImageView_source, 0));
                if (bitmap != null) {
                    bitmaps = singletonList(bitmap);
                    scene = new int[]{0};
                    maxBitmapHeight = bitmaps.get(0).getHeight();
                } else {
                    bitmaps = Collections.emptyList();
                }
            }
        } finally {
            ta.recycle();
        }

        if (initialState == 0) {
            start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxBitmapHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(!isInEditMode()) {
            if (lastFrameInstant == null){
                lastFrameInstant = Instant.now();
            }
            frameTimeMicros = ChronoUnit.MICROS.between(lastFrameInstant, Instant.now());
            lastFrameInstant = Instant.now();

            super.onDraw(canvas);
            if (canvas == null || bitmaps.isEmpty()) {
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
                canvas.drawBitmap(bitmap, getBitmapLeft(width, left), 0, paint);
                left += width;
            }

            if (isStarted && speed != 0) {

                offset -= (abs(speed) / MICROS_PER_SECOND) * frameTimeMicros;
                postInvalidateOnAnimation();
            }
        }
    }

    private Bitmap getBitmap(int sceneIndex) {
        return bitmaps.get(scene[sceneIndex]);
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
            lastFrameInstant = null;
            postInvalidateOnAnimation();
        }
    }

    /**
     * Stop the animation
     */
    @SuppressWarnings("unused")
    public void stop() {
        if (isStarted) {
            isStarted = false;
            lastFrameInstant = null;
            invalidate();
        }
    }
}