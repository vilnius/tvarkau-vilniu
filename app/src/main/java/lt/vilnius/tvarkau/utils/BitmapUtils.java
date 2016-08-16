package lt.vilnius.tvarkau.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class BitmapUtils {


    public static String convertToBase64EncodedString(Uri uri) {

        Bitmap bitmap = createBitmap(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
    }

    public static Bitmap createBitmap(Uri uri) {

        final int requiredWidth = 1600;
        final int requiredHeight = 1600;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String bitmapFilePath = new File(uri.getPath()).getAbsolutePath();
        BitmapFactory.decodeFile(bitmapFilePath, options);
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, options);

        final int bitmapHeight = bitmap.getHeight();
        final int bitmapWidth = bitmap.getWidth();

        if (bitmapHeight > bitmapWidth && bitmapHeight > requiredHeight) {
            return scaleToFitHeight(bitmap, requiredHeight);
        } else if (bitmapWidth > bitmapHeight && bitmapWidth > requiredWidth) {
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