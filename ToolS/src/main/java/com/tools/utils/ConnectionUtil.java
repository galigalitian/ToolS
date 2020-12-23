package com.tools.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TIANWEI
 * @version 创建时间 2015年10月2日 下午6:19:09
 * $Revision$ $Date$
 */
public class ConnectionUtil {
    
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
    public static void main(String[] args) {
    }
    private static Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);
    
    public static Connection getConnection(String url) {
        return Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).timeout(40000).maxBodySize(0);
    }
    
    public static Connection getProxyConnection(String url) {
        return Jsoup.connect(url).proxy(new Proxy(Proxy.Type.SOCKS,
                new InetSocketAddress("127.0.0.1", 10808))).userAgent(USER_AGENT).ignoreContentType(true).timeout(40000).maxBodySize(0);
    }
    
    public static Document getDocument(String url) {
        try {
            return Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).timeout(20000).maxBodySize(0).get();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }
    
    public static Document getDocumentIgnore(String url, Map<String, String> cookies) {
        try {
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).ignoreContentType(true).timeout(120000).cookies(cookies).maxBodySize(0).get();
            return doc;
        } catch (IOException e) {
            System.out.println(url);
            logger.error(e.getMessage());
        }
        return null;
    }
    
 // static block
    static {
        // 设置屏蔽SSL请求告警
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
        }
    }
}
