package com.example.android.okcupidassessment.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.okcupidassessment.R;
import com.example.android.okcupidassessment.presenter.SearchPresenter;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sohail on 2/15/16.
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    // stores the imageviews being recycled
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());

    private ExecutorService executorService;

    private Context context;

    // caching class handles all disk and memory caching
    private ImageCache imageCache;

    public ImageLoader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
        this.context = context;
        imageCache = ImageCache.getInstance();
    }

    /**
     * recycles the image view and if available displays the image from memory
     */
    public void displayImage(String key, ImageView imageView) {

        // store the image view to recycle
        imageViews.put(imageView, key);

        // try to get bitmap from memory cache
        Bitmap bitmap = imageCache.getBitmapFromMemCache(key);
        if (bitmap != null) {

            if (Utils.DEBUG) Log.i("FROM", "FROM MEMORY");

            // remove the placeholder bitmap and set the actual bitmap from memory
            imageView.setImageBitmap(null);
            imageView.setImageBitmap(bitmap);

        } else {

            // if bitmap was not in memory cache,
            // create a task to run in background to load the bitmap
            queuePhoto(key, imageView);

            // set the placeholder image
            imageView.setImageResource(R.drawable.image_holder);
        }
    }

    /**
     * simple creates the photoToLoad objects and adds it to queue
     */
    private void queuePhoto(String path, ImageView imageView) {
        PhotoToLoad photoToLoad = new PhotoToLoad(path, imageView);
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    /**
     * checks if imageView is being reused
     */
    private boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);

        return tag == null || !tag.equals(photoToLoad.path);
    }

    /**
     * checks to see if bitmap is in disk, if not then gets it from network
     */
    private Bitmap getBitmap(String path) {

        // get bitmap from disk, if empty then proceed further
        Bitmap bitmap = imageCache.getBitmapFromDiskCache(path, context);
        if (bitmap != null) {
            if (Utils.DEBUG) Log.i("FROM", "FROM DISK");
            return bitmap;
        }

        bitmap = SearchPresenter.getInstance().fetchImage(path);

        // adds bitmap to disk cache
        imageCache.addBitmapToDiskCache(bitmap, context, path);

        if (Utils.DEBUG) Log.i("FROM", "FROM NULL");

        return bitmap;
    }

    /**
     * simple class that holds the path to bitmap and imageView
     */
    private class PhotoToLoad {
        private String path;
        private ImageView imageView;

        private PhotoToLoad(String path, ImageView imageView) {
            this.path = path;
            this.imageView = imageView;
        }
    }

    /**
     * Runnable task for the queue
     * gets the photo from disk cache if available or using network calls
     * calls on bitmapDisplayer to load the picture
     */
    private class PhotosLoader implements Runnable {

        private final String TAG = this.getClass().getSimpleName();

        private PhotoToLoad photoToLoad;

        private PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) return;

            if (Utils.DEBUG)
                Log.i(TAG + " ON UI THREAD = ",
                        String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

            Bitmap bitmap = getBitmap(photoToLoad.path);

            imageCache.addBitmapToMemCache(photoToLoad.path, bitmap);

            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, photoToLoad);
            AppCompatActivity activity = (AppCompatActivity) photoToLoad.imageView.getContext();
            activity.runOnUiThread(bitmapDisplayer);
        }
    }

    /**
     * used to display bitmap in the UI thread
     */
    private class BitmapDisplayer implements Runnable {

        private final String TAG = this.getClass().getSimpleName();

        private Bitmap bitmap;
        private PhotoToLoad photoToLoad;

        private BitmapDisplayer(Bitmap bitmap, PhotoToLoad photoToLoad) {
            this.bitmap = bitmap;
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {

            if (Utils.DEBUG)
                Log.i(TAG + " ON UI THREAD = ",
                        String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(null);
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else photoToLoad.imageView.setImageResource(R.drawable.image_holder);

        }
    }
}
