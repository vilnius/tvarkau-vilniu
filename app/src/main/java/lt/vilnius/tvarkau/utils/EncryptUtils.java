package lt.vilnius.tvarkau.utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptUtils {

    public static String encrypt(String data){

        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApdx8xnpqootgc0CyGQ83laIot1tUVTl8ORZq4z4atEbcemOWOEjXZ+ecOsHoZfmP17hmV80vusNAijuh7Utq+2kYsO51pzS6GBzh/O4ET3dxbwUA5GG43KCrKAOwurgRJkfjyihmsMGjWC/c1wzHm17agD5ZcKVL2EdYos7wGHa3gkxuxHJzG2L9tYq8OC23IpQlmpOE4Qajw/IdTil6pxe3/ur+CkJRQZdavc1aoHM8nCTXNivrZYLQV4eqhn3KHUwoTFA7BHkOhLdMnUjURQM/JovLJk83R7C5K+9EeFceNiSBuilpXyfurRCVzHyxZ1wTDiFAYfC/NKXEd42rQwIDAQAB";
        byte[] byteKey = Base64.decode(key, Base64.DEFAULT);
        try {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(byteKey));
            return encode(publicKey, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encode(Key publicKey, String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] byteData = data.getBytes();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedByteData = cipher.doFinal(byteData);

        return Base64.encodeToString(encryptedByteData, Base64.NO_WRAP);
    }
}
