package com.q42.android.scrollingimageview;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by thijs on 22-03-16.
 */
public interface ScrollingImageViewBitmapLoader {
    Bitmap loadBitmap(Context context, int resourceId);
}
