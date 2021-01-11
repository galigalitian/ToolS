package com.tools.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;


/**
 * @author TIAN WEI
 * @version 创建时间2009-6-29 下午07:47:15
 * $Revision$ $Date$
 *
 */
public class CookieUtil {
    
//    public static void writeCookie(HttpServletResponse response, Integer timeout) {
//        String uuid = UUIDGenerator.getUUID();
//        Cookie cookie = new Cookie("mttp", uuid);
//        cookie.setMaxAge(timeout);
//        cookie.setDomain("yikedy.co");
//        cookie.setPath("/");
//        response.addCookie(cookie);
//    }
    
    public static void writeCmsCookie(HttpServletResponse response, Integer timeout, String jtun, String jtpsd) {
        Cookie userNameCookie = new Cookie("jtun", jtun);
        userNameCookie.setMaxAge(timeout);
        userNameCookie.setDomain("yikedy.co");
        userNameCookie.setPath("/");
        response.addCookie(userNameCookie);
        
        Cookie passwordCookie = new Cookie("jtpsd", jtpsd);
        passwordCookie.setMaxAge(timeout);
        passwordCookie.setDomain("yikedy.co");
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);
    }
    
    public static void logoutCmsCookie(HttpServletResponse response) {
        Cookie userNameCookie = new Cookie("jtun", "");
        userNameCookie.setMaxAge(0);
        userNameCookie.setDomain("yikedy.co");
        userNameCookie.setPath("/");
        
        Cookie passwordCookie = new Cookie("jtpsd", "");
        passwordCookie.setMaxAge(0);
        passwordCookie.setDomain("yikedy.co");
        passwordCookie.setPath("/");
        response.addCookie(userNameCookie);
        response.addCookie(passwordCookie);
    }
    
    public static String writeBlackCookie(HttpServletResponse response, Integer timeout) {
        String uuid = UUIDGenerator.getUUID();
        Cookie cookie = new Cookie("mttp", uuid);
        cookie.setMaxAge(timeout);
        cookie.setDomain("yikedy.co");
        cookie.setPath("/");
        response.addCookie(cookie);
        return uuid;
    }
    
    public static void writeCookie(HttpServletResponse response, Integer timeout, String mkid, String mkpsd) {
        Cookie userIdCookie = new Cookie("mkid", mkid);
        userIdCookie.setMaxAge(timeout);
        userIdCookie.setDomain("yikedy.co");
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);
        
        Cookie passwordCookie = new Cookie("mkpsd", mkpsd);
        passwordCookie.setMaxAge(timeout);
        passwordCookie.setDomain("yikedy.co");
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);
    }
    
    public static void logoutCookie(HttpServletResponse response) {
        Cookie useridCookie = new Cookie("mkid", "");
        useridCookie.setMaxAge(0);
        useridCookie.setDomain("yikedy.co");
        useridCookie.setPath("/");
        
        Cookie passwordCookie = new Cookie("mkpsd", "");
        passwordCookie.setMaxAge(0);
        passwordCookie.setDomain("yikedy.co");
        passwordCookie.setPath("/");
        
        response.addCookie(useridCookie);
        response.addCookie(passwordCookie);
    }
    
    public static String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                value = cookie.getValue();
            }
        }
        return value;
    }
    
    public static String getMd5(String password) {
        try {
            MessageDigest message = MessageDigest.getInstance("MD5");
            password = HexBin.encode(message.digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return password;
    }
}
