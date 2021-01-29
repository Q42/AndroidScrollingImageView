[![](https://jitpack.io/v/q42/androidscrollingimageview.svg)](https://jitpack.io/#q42/androidscrollingimageview)

# Scrolling Image View

An Android view for displaying repeated continuous side scrolling images. This can be used to create a parallax animation effect.

## Example
![Example](https://raw.githubusercontent.com/Q42/AndroidScrollingImageView/master/preview.gif)

## Installation
*Step 1.* Add the JitPack repository to your project `build.gradle` file
```gradle
allprojects {
    repositories {
        // ~~~
        maven { url 'https://jitpack.io' }
    }
}
```
*Step 2.* Add the dependency in the form
```gradle
dependencies {
    implementation 'com.github.Q42:AndroidScrollingImageView:1.3.4'
}
```

## Sample app
Please see the sample app for some examples

![Sample App](https://raw.githubusercontent.com/Q42/AndroidScrollingImageView/master/sample_app.png)

## Usage
In your Android layout file add:
```xml
<com.q42.android.scrollingimageview.ScrollingImageView
    android:id="@+id/scrolling_background"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    scrolling_image_view:speed="60dp"
    scrolling_image_view:contiguous="false"
    scrolling_image_view:source="@drawable/scrolling_background" />
```

There are three attributes for the `ScrollingImageView`:
* `speed` is the number of `dp`'s to move the drawable per second (may be a negative number)
* `source` is the drawable to paint. May refer to an array of drawables
* `contiguous` When source is an array of drawables, `contiguous` determines their ordering.

  false (default) for random ordering, true for the same order as in the array

Don't forget to add the namespace to your root XML element
```xml
xmlns:scrolling_image_view="http://schemas.android.com/apk/res-auto"
```

In your Java code, you can start and stop the animation like this:
```java
ScrollingImageView scrollingBackground = (ScrollingImageView) loader.findViewById(R.id.scrolling_background);
scrollingBackground.stop();
scrollingBackground.start();
```

## Parallax effect
In order to achieve a parallax effect, you can stack multiple `ScrollingImageView`'s in a `FrameLayout` with different speeds. For example:
```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

  <com.q42.android.scrollingimageview.ScrollingImageView
      android:id="@+id/scrolling_background"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      scrolling_image_view:speed="60dp"
      scrolling_image_view:source="@drawable/scrolling_background" />
      
  <com.q42.android.scrollingimageview.ScrollingImageView
      android:id="@+id/scrolling_foreground"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      scrolling_image_view:speed="150dp"
      scrolling_image_view:source="@drawable/scrolling_foreground" />
</FrameLayout>
```
