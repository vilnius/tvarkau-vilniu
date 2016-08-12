package lt.vilnius.tvarkau.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class BitmapUtils {

    public static Bitmap createBitmap(Uri uri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, 1000, 1000);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        if (bitmap.getHeight() < 1000 || bitmap.getWidth() < 1000) {
            return bitmap;
        } else {
            if (bitmap.getHeight() > bitmap.getWidth()) {
                return scaleToFitHeight(bitmap, 1000);
            } else {
                return scaleToFitWidth(bitmap, 1000);
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("Image initial height", String.format("%d", height));
        Log.d("Image initial width", String.format("%d", width));
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }
}