package com.tools.encryption.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;

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
 
    
    public static String upperCaseEncrypt16(String encryptStr) {
        String eStr = encrypt16(encryptStr);
        return eStr.toUpperCase();
    }
    
    public static String encrypt16(String encryptStr) {
        return encrypt32(encryptStr).substring(8, 24);
    }
    
    public static void main(String args[]) {
        String encryptStr = "123456";
        System.out.println(EncryptionController.encrypt32(encryptStr));
        System.out.println(EncryptionController.encrypt16(encryptStr));
    }
}
