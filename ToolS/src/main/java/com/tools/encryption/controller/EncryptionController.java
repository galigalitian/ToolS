package com.tools.encryption.controller;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class EncryptionController {
    
    /**
     * base64 加密
     * @param plainText
     * @return
     */
    public String base64Encoder(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }
    
    /**
     * base64 解密
     * @param plainText
     * @return
     */
    public String base64Decode(String plainText) {
        byte[] decodeByte = Base64.getDecoder().decode(plainText);
        try {
            return new String(decodeByte, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * md5大写32位加密
     * @param encryptStr
     * @return
     */
    public static String upperCaseEncrypt32(String encryptStr) {
        String eStr = encrypt32(encryptStr);
        return eStr.toUpperCase();
    }
    
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }
 
    /**
     * md5大写16位加密
     * @param encryptStr
     * @return
     */
    public static String upperCaseEncrypt16(String encryptStr) {
        String eStr = encrypt16(encryptStr);
        return eStr.toUpperCase();
    }
    
    public static String encrypt16(String encryptStr) {
        return encrypt32(encryptStr).substring(8, 24);
    }
    
    /**
     * AES加密生成key
     * @return
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();
            return Hex.encodeHexString(byteKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * AES加密
     * @param thisKey
     * @param data
     * @return
     */
    public static String encode(String thisKey, String data) {
        try {
            // 转换KEY
            Key key = new SecretKeySpec(Hex.decodeHex(thisKey),"AES");

            // 加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(data.getBytes());
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * AES解密
     * @param thisKey
     * @param data
     * @return
     */
    public static String decode(String thisKey, String data) {
        try {
            // 转换KEY
            Key key = new SecretKeySpec(Hex.decodeHex(thisKey),"AES");
            // 解密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Hex.decodeHex(data));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void main(String args[]) {
        String encryptStr = "123456";
        System.out.println(EncryptionController.encrypt32(encryptStr));
        System.out.println(EncryptionController.encrypt16(encryptStr));
    }
}
