package lt.vilnius.tvarkau.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class ImageUtils {

    @Nullable
    public static String getExifTimeStamp(@NonNull File file) {
        try {
            return new ExifInterface(file.getPath()).getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static String convertToBase64EncodedString(Context context, File file) {
        Bitmap bitmap = createBitmap(context, file);

        if (bitmap == null)
            return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
    }

    @Nullable
    private static Bitmap createBitmap(Context context, File file) {
        final int requiredWidth = 1600;
        final int requiredHeight = 1600;

        try {
            return Glide.with(context)
                    .load(file)
                    .asBitmap()
                    .fitCenter()
                    .into(requiredWidth, requiredHeight)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.w(e, "Unable to create bitmap from " + file);

            return null;
        }
    }
}