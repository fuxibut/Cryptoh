package com.secusec.cryptoh;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class Blowfish {

    /*
     * Decodes a text with Base64 and decrypts it
     */
    public static String decrypt(String strEncrypted, String strKey) throws Exception {
        if (strEncrypted == null || strEncrypted.isEmpty())
            throw new IllegalArgumentException("Cleartext null or empty.");
        if (strKey == null || strKey.isEmpty())
            throw new IllegalArgumentException("Key null or empty.");
        byte[] decodedBytes = Base64.getDecoder().decode(strEncrypted);
        SecretKeySpec sKeySpec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        return new String(cipher.doFinal(decodedBytes));
    }

    /*
     * Encrypts a text and encodes it with Base64
     */
    public static String encrypt(String strClearText, String strKey) throws Exception {
        if (strClearText == null || strClearText.isEmpty())
            throw new IllegalArgumentException("Cleartext null or empty.");
        if (strKey == null || strKey.isEmpty() || strKey.length() < 8)
            throw new IllegalArgumentException("Key null, empty or too short (min 8).");
        SecretKeySpec sKeySpec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        byte[] encrypted = cipher.doFinal(strClearText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
