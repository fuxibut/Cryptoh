package com.secusec.cryptoh;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Blowfish {

    public static String decrypt(String strEncrypted, String strKey) throws Exception {
        if (strEncrypted == null || strEncrypted.isEmpty())
            throw new IllegalArgumentException("Cleartext null or empty.");
        if (strKey == null || strKey.isEmpty())
            throw new IllegalArgumentException("Key null or empty.");
        byte[] decodedBytes = Base64.getDecoder().decode(strEncrypted);
        SecretKeySpec sKeySpec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        String decrypted = new String(cipher.doFinal(decodedBytes));
        return decrypted;
    }

    public static String encrypt(String strClearText, String strKey) throws Exception {
        if (strClearText == null || strClearText.isEmpty())
            throw new IllegalArgumentException("Cleartext null or empty.");
        if (strKey == null || strKey.isEmpty() || strKey.length() < 8)
            throw new IllegalArgumentException("Key null, empty or too short (min 8).");
        SecretKeySpec sKeySpec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        byte[] encrypted = cipher.doFinal(strClearText.getBytes());
        String encoded = Base64.getEncoder().encodeToString(encrypted);
        return encoded;
    }
}
