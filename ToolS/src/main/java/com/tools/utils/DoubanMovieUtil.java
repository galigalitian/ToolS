//package com.tools.utils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.tools.cms.cookies.CmsCookiesUtilsController;
//
//@Component
//public class DoubanMovieUtil {
//    
//    private static Map<String, String> cookies = new HashMap<String, String>();
//    static Logger logger = LoggerFactory.getLogger(DoubanMovieUtil.class);
//    
//    @Autowired
//    public DoubanMovieUtil(CmsCookiesUtilsController cmsCookiesUtilsController) {
//        cookies = cmsCookiesUtilsController.getDoubanCookies();
//    }
//    
//    public static Map<String, String> getCookies() {
//        return cookies;
//    }
//    
//    public static void setCookies(Map<String, String> cookies) {
//        DoubanMovieUtil.cookies = cookies;
//    }
//    
//    public static String returnDoubanId(String douban_url) {
//        try {
//            String[] douban_urls = douban_url.split("/");
//            String douban_id = douban_urls[douban_urls.length - 1];
//            return douban_id;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "0";
//        }
//    }
//    
//    public static void main(String args[]) {
//        System.out.println(returnDoubanId("http://movie.douban.com/subject/30306570"));
//        
//        //getDetailMovie("https://movie.douban.com/subject/26964443");
////        String href = "14人看过";
////        Pattern pattern = Pattern.compile("[0-9]+");
////        Matcher matcher = pattern.matcher(href);
////        if (matcher.find()) 
////            System.out.println(matcher.group());
////        String aa = "幸存者 第三十二季";
////        System.out.println(aa.substring(0, aa.indexOf(" 第")));
//    }
//}
