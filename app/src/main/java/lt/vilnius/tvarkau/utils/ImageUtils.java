package lt.vilnius.tvarkau.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class ImageUtils {

    public static String convertToBase64EncodedString(Uri uri) {

        Bitmap bitmap = createBitmap(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
    }

    private static Bitmap createBitmap(Uri uri) {

        final int requiredWidth = 1600;
        final int requiredHeight = 1600;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String bitmapFilePath = new File(uri.getPath()).getAbsolutePath();
        BitmapFactory.decodeFile(bitmapFilePath, options);

        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, options);

        Bitmap rotatedBitmap = rotateBitmap(bitmap, bitmapFilePath);

        final int bitmapHeight = rotatedBitmap.getHeight();
        final int bitmapWidth = rotatedBitmap.getWidth();

        if (bitmapHeight > bitmapWidth && bitmapHeight > requiredHeight) {
            return scaleToFitHeight(rotatedBitmap, requiredHeight);
        } else if (bitmapWidth > bitmapHeight && bitmapWidth > requiredWidth) {
            return scaleToFitWidth(rotatedBitmap, requiredWidth);
        } else {
            return bitmap;
        }
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            Timber.e(e);
        }
        if (exif != null) {
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotationAngle = 90;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotationAngle = 180;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotationAngle = 270;
            }

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

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

    private static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    public static String getPhotoPathFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            if (path != null) {
                cursor.close();
                return path;
            } else {
                // TODO Search for solution to get image path in other ways
                return null;
            }
        } else {
            return uri.getPath();
        }
    }

    public static Uri getTakenPhotoFileUri(Activity activity, String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TvarkauVilniu");

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Timber.d("Failed to create directory");
            }

            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}