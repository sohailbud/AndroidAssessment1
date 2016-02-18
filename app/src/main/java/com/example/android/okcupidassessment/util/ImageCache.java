package com.example.android.okcupidassessment.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Sohail on 2/15/16.
 */
public class ImageCache {

    private final String TAG = this.getClass().getSimpleName();

    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
    private int memCacheSize = DEFAULT_MEM_CACHE_SIZE;

    private static ImageCache imageCache = null;

    // current allocated size
    private long size = 0;

    private Map<String, Bitmap> memoryCache;

    public static ImageCache getInstance() {
        if (imageCache == null) imageCache = new ImageCache();
        return imageCache;
    }

    private ImageCache() {
        // set mem cache to 1/4th of the app memory
        setMemCacheSize(0.25f);

        // initialize memory cache
        initMemCache();
    }

    /**
     * Initialize the cache
     */
    public void initMemCache() {
        memoryCache = Collections.synchronizedMap(
                new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
    }

    public void addBitmapToMemCache(String data, Bitmap value) {
        if (data == null || value == null) return;

        try {
            // if mem cache already contains the bitmap with given id,
            // reduce the size so it can be replaced with new size
            if (memoryCache.containsKey(data))
                size -= getSizeInBytes(memoryCache.get(data));

            // add bitmap to cache, and increase the size value
            memoryCache.put(data, value);
            size += getSizeInBytes(value);

            //
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * adds the bitmap cache directory provided by the system
     *
     * @param bitmap  bitmap to add
     * @param context to get cache dir
     * @param path    path of current location
     */
    public void addBitmapToDiskCache(Bitmap bitmap, Context context, String path) {

        if (Utils.DEBUG)
            Log.i(TAG + " ON UI THREAD = ",
                    String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

        try {
            // use the last path segment for name
            String filename = Uri.parse(path).getLastPathSegment();
            File file = new File(context.getCacheDir(), filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            // compress the bitmap and create a byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // write the file to directory
            fileOutputStream.write(byteArray, 0, byteArray.length);

            // close the stream
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets bitmap from memory using the key
     *
     * @param data
     * @return
     */
    public Bitmap getBitmapFromMemCache(String data) {
        try {
            return !memoryCache.containsKey(data) ? null : memoryCache.get(data);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets bitmap from the disk cache directory
     *
     * @param url path of the original image
     * @param context to get cache dir
     */
    public Bitmap getBitmapFromDiskCache(String url, Context context) {

        if (Utils.DEBUG)
            Log.i(TAG + " ON UI THREAD = ",
                    String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

        if (url == null) return null;

        String filename = Uri.parse(url).getLastPathSegment();
        if (filename == null) return null;

        File file = new File(context.getCacheDir(), filename);
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * Sets the memory cache size based on a percentage of the max available VM memory.
     *
     * @param percent Percent of available app memory to use to size memory cache
     */
    public void setMemCacheSize(float percent) {
        if (percent < 0.1f || percent > 0.8f)
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                    + "between 0.01 and 0.8 (inclusive)");

        memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory());
    }

    /**
     * clears memory cache and sets size to default
     */
    public void clearMemCache() {
        try {
            memoryCache.clear();
            size = 0;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * calculates the size of provided bitmap
     */
    private long getSizeInBytes(Bitmap bitmap) {
        return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * after adding each bitmap, check size to make sure allocated size is less than max size available
     * if required, delete entries until
     */
    private void checkSize() {

        if (size > memCacheSize) {
            Iterator<Map.Entry<String, Bitmap>> iterator = memoryCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Bitmap> entry = iterator.next();
                size -= getSizeInBytes(entry.getValue());
                iterator.remove();
                if (size <= memCacheSize) break;
            }
        }
    }

}
