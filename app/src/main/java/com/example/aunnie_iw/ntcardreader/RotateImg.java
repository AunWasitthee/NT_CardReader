package com.example.aunnie_iw.ntcardreader;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Aunnie-IW on 30/6/2560.
 */

public class RotateImg {

    public static Bitmap Rotate(String photoPath, Bitmap bitmap)  {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(e.toString(), "Rotage: ");
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d("90", "Rotage: ");
                bitmap= rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap=rotateImage(bitmap, 180);
                Log.d("180", "Rotage: ");
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap=rotateImage(bitmap, 270);
                Log.d("270", "Rotage: ");
                break;

            case ExifInterface.ORIENTATION_NORMAL:
                Log.d("No", "Rotage: ");
            default:
                break;
        }
        Log.d("bit", "Rotage: ");
        float aspectRatio = bitmap.getWidth() /
                (float) bitmap.getHeight();

        int width = 1024;
        int height = Math.round(width / aspectRatio);
        Log.d(String.valueOf(bitmap.getWidth()), "rotateImage: source.getWidth()");
        Log.d(String.valueOf(bitmap.getHeight()), "rotateImage: source.getHeight()");
        Log.d(String.valueOf(aspectRatio), "rotateImage: aspectRatio");
        Log.d(String.valueOf(width), "rotateImage: width");
        Log.d(String.valueOf(height), "rotateImage: height");
        bitmap = Bitmap.createScaledBitmap(
                bitmap, width, height, false);
        return bitmap;
        //viewImage.setImageBitmap(bitmap);
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Log.d(String.valueOf(angle), "rotateImage: ");
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.d("M", "rotateImage: ");
//        float aspectRatio = source.getWidth() /
//                (float) source.getHeight();
//
//        int width = 1024;
//        int height = Math.round(width / aspectRatio);
//        Log.d(String.valueOf(source.getWidth()), "rotateImage: source.getWidth()");
//        Log.d(String.valueOf(source.getHeight()), "rotateImage: source.getHeight()");
//        Log.d(String.valueOf(aspectRatio), "rotateImage: aspectRatio");
//        Log.d(String.valueOf(width), "rotateImage: width");
//        Log.d(String.valueOf(height), "rotateImage: height");
//        source = Bitmap.createScaledBitmap(
//                source, width, height, false);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
