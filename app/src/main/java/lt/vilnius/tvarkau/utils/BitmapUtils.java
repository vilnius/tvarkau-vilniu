package lt.vilnius.tvarkau.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

public class BitmapUtils {

    public static Bitmap createBitmap(Uri uri) {

        final int requiredWidth = 1600;
        final int requiredHeight = 1600;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);

        if (bitmap.getHeight() > bitmap.getWidth() && bitmap.getHeight() > requiredHeight) {
            return scaleToFitHeight(bitmap, requiredHeight);
        } else if (bitmap.getWidth() > bitmap.getHeight() && bitmap.getWidth() > requiredWidth) {
            return scaleToFitWidth(bitmap, requiredWidth);
        } else {
            return bitmap;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > width && height > reqHeight) {
            final int halfHeight = height / 2;
            while ((halfHeight / inSampleSize) >= reqHeight) {
                inSampleSize *= 2;
            }
        } else if (width > height && width > reqWidth) {
            final int halfWidth = width / 2;
            while (halfWidth / inSampleSize >= reqWidth) {
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