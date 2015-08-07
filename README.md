# Scrolling Image View

An Android view for displaying repeated continuous side scrolling images. This can be used to create a parallax animation effect.

## Example
![Example](https://raw.githubusercontent.com/Q42/AndroidScrollingImageView/master/preview.gif)

##Installation
*Step 1.* Add the JitPack repository to your build file
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}
```
*Step 2.* Add the dependency in the form
```gradle
dependencies {
    compile 'com.github.Q42:AndroidScrollingImageView:1.0'
}
```

## Usage
In your Android layout file add:
```xml
<com.q42.android.scrollingimageview.ScrollingImageView
    android:id="@+id/scrolling_background"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    scrolling_image_view:speed="1dp"
    scrolling_image_view:src="@drawable/scrolling_background" />
```

Don't forget to add the namespace to your root XLM element
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
      scrolling_image_view:speed="1dp"
      scrolling_image_view:src="@drawable/scrolling_background" />
      
  <com.q42.android.scrollingimageview.ScrollingImageView
      android:id="@+id/scrolling_foreground"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      scrolling_image_view:speed="2.5dp"
      scrolling_image_view:src="@drawable/scrolling_foreground" />
</FrameLayout>
```
