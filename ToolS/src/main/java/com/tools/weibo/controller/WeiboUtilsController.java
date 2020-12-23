package com.tools.weibo.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.tools.utils.ConnectionUtil;

@Component
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class WeiboUtilsController {
    @Autowired
    private MongoTemplate mongoTemplate;
//    @Autowired
//    private RedisUtil redisUtil;
    
    private static String mblogUrl = "https://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6"
            + "&profile_ftype=1&is_all=1&pl_name=&script_uri=&feed_type=0&__rnd=";
    private static String longContentUrl = "https://weibo.com/p/aj/mblog/getlongtext?ajwvr=6&is_settop&is_sethot&is_setfanstop&is_setyoudao&__rnd="; //得到微博长内容的链接
    private static String commentUrl = "https://weibo.com/aj/v6/comment/big?ajwvr=6&from=singleWeiBo&__rnd=";
    //private static final String client_ID = "2231125779";
    private static final String weiboCookiesMapKey = "weiboCookies";
    
    private final static String weiboLoginUserName = "yikedy@gmail.com";
    private final static String weiboLoginPassword = "qiuli19850903";
    private Logger logger = LoggerFactory.getLogger(WeiboUtilsController.class);
    
    /*weibo的登录cookies*/
//    private Map<String, String> getWeiboCookies() {
//        Map<String, String> cookies = cmsCookiesUtilsController.getWeiboCookies();
//        return cookies;
//    }
    /**
     * 得到爬取用户微博的基础信息
     * @param cookiesMap
     * @return
     */
    private String getPageInfo(Map<String, String> cookiesMap, String userUrl) {
        if (userUrl.indexOf("weibo.com") != -1) {
            String userUrls[] = userUrl.split("weibo.com/");
            if (userUrls.length > 1)
                userUrl = userUrls[1];
        }
        String url = "https://weibo.com/" + userUrl;
        try {
            Connection urlCon = ConnectionUtil.getConnection(url);
            Document doc = urlCon.cookies(cookiesMap).get();
            cookiesMap.putAll(urlCon.response().cookies());
            Elements scriptEls = doc.select("script");
            if (scriptEls.html().indexOf("location.replace") != -1) {
                Pattern pattern = Pattern.compile("location\\.replace\\(\"(.*?)\"\\)");
                Matcher matcher = pattern.matcher(scriptEls.html());
                while (matcher.find()) {
                    url = matcher.group(1);
                }
                
                doc = ConnectionUtil.getConnection(url).referrer("https://www.weibo.com").cookies(cookiesMap).get();
            }
            Map<String, String> configMap = new HashMap<String, String>();
            for (Element scriptEl : scriptEls) {
                String scriptHtml = scriptEl.html();
                if (scriptHtml.indexOf("$CONFIG[") != -1) {
                    Pattern pattern = Pattern.compile("\\$CONFIG\\['(.*?)'\\]='(.*?)'");
                    Matcher matcher = pattern.matcher(scriptHtml);
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String val = matcher.group(2);
                        configMap.put(key, val);
                    }
                }
            }
            
            if (configMap == null || configMap.isEmpty()) {
                //cookiesMap = autoLoginWeibo();
                //return getPageInfo(cookiesMap, userUrl);
                //alert错误警告
            }
            
            String domain = configMap.get("domain");
            String page_id = configMap.get("page_id");
            String jointStr = "&domain=" + domain + "&id=" + page_id + "&domain_op=" + domain;
            //System.out.println(jointStr);
            return jointStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 得到微博的长内容
     * @param cookiesMap
     * @return
     */
    private String longContent(Map<String, String> cookiesMap, String mid) {
        String url = longContentUrl + "&mid=" + mid;
        Connection conn = ConnectionUtil.getConnection(url).cookies(cookiesMap);
        try {
            conn.get();
            String bodyHtml = conn.response().body();
            if (StringUtils.isNotBlank(bodyHtml)) {
                JSONObject jsonObj = new JSONObject(bodyHtml);
                if (jsonObj.has("data")) {
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    if (dataObj != null && dataObj.has("html")) {
                        String html = dataObj.getString("html");
                        return html;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /*根据判断html中是否有链接返回代码*/
//    private Map<String, Object> ifHasUrlToReturnText(String html, Map<String, String> cookiesMap, String mid) {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        Document htmlDoc = Jsoup.parse(html);
//        Elements allHrefEls = htmlDoc.select("a");
//        String longContentText = "";
//        List<String> commentTextList = new ArrayList<String>();
//        boolean f = false;
//        if (allHrefEls != null && allHrefEls.size() > 0) {
//            for (Element allHrefEl : allHrefEls) { //循环长微博中的所有链接，找到百度网盘的链接
//                String href = allHrefEl.attr("href");
//                if (StringUtils.isBlank(href) || href.indexOf("weibo") != -1 || href.indexOf("javascript") != -1) continue;
//                else { //在这里判断是否是百度网盘的链接
//                    String url_long = shortToLongUrl(href);
//                    if (url_long != null) {
//                        if (url_long.indexOf("pan.baidu.com") != -1) {
//                            allHrefEl.html(url_long);
//                            f = true;
//                        }
//                        if (url_long.indexOf("magnet:?xt=") != -1) {
//                            allHrefEl.html(url_long);
//                            f = true;
//                        }
//                        if (url_long.indexOf("movie.douban.com") != -1) {
//                            allHrefEl.html(url_long);
//                            resultMap.put("doubanUrl", url_long);
//                        }
//                        if (url_long.indexOf("zzrbl.com") != -1) { //猪猪字幕组特殊处理
//                            allHrefEl.html(url_long);
//                            try {
//                                Integer otherId = cmsSpiderZZBase.crawlZZMovieDetail(url_long);
//                                resultMap.put("otherId", otherId);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else continue;
//                }
//            }
//            if (!f)
//                commentTextList.addAll(urlFromComment(cookiesMap, mid));
//        } else {
//            commentTextList.addAll(urlFromComment(cookiesMap, mid));
//        }
//        if (commentTextList != null && commentTextList.size() >0) {
//            if (longContentText.equals("")) longContentText = htmlDoc.body().text();
//            for (String commentText : commentTextList) {
//                longContentText = longContentText + "<br>" + commentText;
//            }
//        }
//        if (StringUtils.isBlank(longContentText)) {
//            String htext = htmlDoc.text();
//            if (f || htext.indexOf("magnet:?xt=") != -1) {
//                longContentText = htext;
//            }
//        }
//        resultMap.put("longContentText", longContentText);
//        return resultMap;
//    }
    
    /*根据判断html中是否有链接返回代码*/
//    private Map<String, Object> ifHasUrlToReturnText(Element element) {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        Elements allHrefEls = element.select("a");
//        String longContentText = null;
//        if (allHrefEls != null && allHrefEls.size() > 0) {
//            boolean f = false;
//            for (Element allHrefEl : allHrefEls) { //循环长微博中的所有链接，找到百度网盘的链接
//                String href = allHrefEl.attr("href");
//                if (StringUtils.isBlank(href) || href.indexOf("weibo") != -1 || href.indexOf("javascript") != -1) continue;
//                else { //在这里判断是否是百度网盘的链接
//                    String url_long = shortToLongUrl(href);
//                    if (url_long != null && url_long.indexOf("weibo") == -1) {
//                        if (url_long.indexOf("pan.baidu.com") != -1) {
//                            allHrefEl.html(url_long);
//                            f = true;
//                        }
//                        if (url_long.indexOf("magnet:?xt=") != -1) {
//                            allHrefEl.html(url_long);
//                            f = true;
//                        }
//                        if (url_long.indexOf("movie.douban.com") != -1) {
//                            allHrefEl.html(url_long);
//                            resultMap.put("doubanUrl", url_long);
//                        }
//                        if (url_long.indexOf("zzrbl.com") != -1) { //猪猪字幕组特殊处理
//                            allHrefEl.html(url_long);
//                            try {
//                                Integer otherId = cmsSpiderZZBase.crawlZZMovieDetail(url_long);
//                                resultMap.put("otherId", otherId);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else continue;
//                }
//            }
//            if (StringUtils.isBlank(longContentText) && f) longContentText = element.text();
//        }
//        resultMap.put("longContentText", longContentText);
//        return resultMap;
//    }
    /**
     * 从评论中得到链接
     * @param cookiesMap
     * @param mid
     * @return
     */
    /*private List<String> urlFromComment(Map<String, String> cookiesMap, String mid) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int p = 1;
        int count = 0;
        int commentSize = 0;
        boolean a = true;
        List<String> commentTextList = new ArrayList<String>();
        Map<String, Integer> hasCommentIdMap = new HashMap<String, Integer>();
        while (a && p < 8) {
            String url = commentUrl + "&page=" + p + "&id=" + mid;
            //System.out.println("------------  " + url);
            Connection conn = ConnectionUtil.getConnection(url).cookies(cookiesMap);
            String bodyHtml = "";
            try {
                conn.get();
                bodyHtml = conn.response().body();
                //System.out.println(bodyHtml);
                if (StringUtils.isNotBlank(bodyHtml)) {
                    JSONObject jsonObj = new JSONObject(bodyHtml);
                    if (jsonObj.has("data")) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        if (dataObj != null && dataObj.has("count")) {
                            String countStr = dataObj.get("count") + "";
                            if (StringUtils.isNotBlank(countStr)) {
                                count = Integer.parseInt(countStr);
                            }
                        }
                        //System.out.println("----------  count：" + count);
                        if (dataObj != null && dataObj.has("html")) {
                            String html = dataObj.getString("html");
                            Document htmlDoc = Jsoup.parse(html);
                            if (htmlDoc.select("div[comment_id]").size() == 0) {
                                a = false;
                                break;
                            } else {
                                Elements commentIdEls = htmlDoc.select("div[comment_id]");
                                String lastCommentId = commentIdEls.get(commentIdEls.size() - 1).attr("comment_id");
                                if (!hasCommentIdMap.containsKey(lastCommentId)) {
                                    hasCommentIdMap.clear();
                                    hasCommentIdMap.put(lastCommentId, 1);
                                } else {
                                    Integer hasCommentIdCount = hasCommentIdMap.get(lastCommentId);
                                    hasCommentIdMap.put(lastCommentId, hasCommentIdCount++);
                                    if (hasCommentIdCount > 1) {
                                        hasCommentIdMap.clear();
                                        a = false;
                                        break;
                                    }
                                }
                                commentSize = commentSize + commentIdEls.size();
                            }
                            //System.out.println("--------  commentSize：" + commentSize);
                            Elements allWB_textEls = htmlDoc.select("div.WB_text");
                            //System.out.println(allWB_textEls.html());
                            if (allWB_textEls != null && allWB_textEls.size() > 0) {
                                for (Element allWB_textEl : allWB_textEls) {
                                    Elements allHrefEls = allWB_textEl.select("a");
                                    if (allHrefEls != null && allHrefEls.size() > 0) {
                                        for (Element allHrefEl : allHrefEls) { //循环长微博中的所有链接，找到百度网盘的链接
                                            String href = allHrefEl.attr("href");
                                            //System.out.println(href);
                                            if (StringUtils.isBlank(href) || href.indexOf("javascript") != -1 || href.indexOf("weibo") != -1) continue;
                                            else { //在这里判断是否是百度网盘的链接
                                                String url_long = shortToLongUrl(href);
                                                if (url_long != null && url_long.indexOf("pan.baidu.com") != -1) {
                                                    allHrefEl.html(url_long);
                                                    commentTextList.add(allWB_textEl.text());
                                                } else continue;
                                            }
                                        }
                                    }
                                }
                            }
                            if (commentSize >= count) {
                                a = false;
                                break;
                            }
                        } else {
                            a = false;
                            break;
                        }
                    } else {
                        a = false;
                        break;
                    }
                } else {
                    a = false;
                    break;
                }
            } catch (JSONException jsonE) {
                jsonE.printStackTrace();
                if (count < 2) {
                    try {
                        Thread.sleep(3000);
                        cookiesMap = autoLoginWeibo();
                        redisUtil.incrExpire("WeiboUtilsController.urlFromComment#" + mid, 1, 1000 * 60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return urlFromComment(cookiesMap, mid);
                }
            } catch (IOException e) {
                e.printStackTrace();
                a = false;
                break;
            } finally {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p++;
            }
        }
        return commentTextList;
    }*/
    
    
    /**
     * 从评论中得到链接
     * @param cookiesMap
     * @param mid
     * @return
     */
    //@Test
    /*public void testUrlFromComment() {
        Map<String, String> cookiesMap = cmsCookiesUtilsController.getWeiboCookies();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int p = 1;
        int count = 0;
        int commentSize = 0;
        boolean a = true;
        while (a) {
            String url = commentUrl + "&page=" + p + "&id=4415327318605441";
            p++;
            Connection conn = ConnectionUtil.getConnection(url).cookies(cookiesMap);
            String bodyHtml = "";
            try {
                conn.get();
                bodyHtml = conn.response().body();
                if (StringUtils.isNotBlank(bodyHtml)) {
                    JSONObject jsonObj = new JSONObject(bodyHtml);
                    if (jsonObj.has("data")) {
                        JSONObject dataObj = jsonObj.getJSONObject("data");
                        if (dataObj != null && dataObj.has("count")) {
                            String countStr = dataObj.get("count") + "";
                            if (StringUtils.isNotBlank(countStr)) {
                                count = Integer.parseInt(countStr);
                            }
                        }
                        if (dataObj != null && dataObj.has("html")) {
                            String html = dataObj.getString("html");
                            Document htmlDoc = Jsoup.parse(html);
                            if (htmlDoc.select("div[comment_id]").size() == 0) {
                                a = false;
                                break;
                            } else {
                                commentSize = commentSize + htmlDoc.select("div[comment_id]").size();
                                if (commentSize >= count) {
                                    a = false;
                                    break;
                                }
                            }
                            
                            //return commentTextList;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(url + "  =============  " + bodyHtml);
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //return null;
    }*/
    /**
     * 微博短链转长链
     * @param shortUrl
     * @return
     */
    private String shortToLongUrl(String shortUrl) {
        if (shortUrl.indexOf("javascript:") != -1) {
            logger.error("=========== 短链转长链出现javascript");
        }
        //StringBuffer toUrlBuffer = new StringBuffer("https://api.weibo.com/2/short_url/expand.json?source=");
//        StringBuffer toUrlBuffer = new StringBuffer("http://api.t.sina.com.cn/short_url/expand.json?source=");
//        toUrlBuffer.append(client_ID).append("&url_short=").append(shortUrl);
//        String toUrl = toUrlBuffer.toString();
//        System.out.println(toUrl);
        Connection conn = ConnectionUtil.getConnection(shortUrl);
        try {
            Document doc = conn.get();
            String longUrl = doc.baseUri();
//            String jsonBody = conn.response().body();
//            JSONArray urlsJsonArr = new JSONArray(jsonBody);
            //JSONObject jsonObj = new JSONObject(jsonBody);
            //JSONArray urlsJsonArr = jsonObj.getJSONArray("urls");
            if (StringUtils.isNotBlank(longUrl)) {
//                JSONObject urlJsonObj = urlsJsonArr.getJSONObject(0);
//                String url_long = urlJsonObj.getString("url_long");
                return longUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Map<String, String> getCookies() {
        Map<String, String> cookies = Maps.newHashMap();
        String cookiesStr = "tid=hJPPDJ+3K+lSqxGbk8K36jCCKa48WrGkMOIZjI2v0I4=__095;SINAGLOBAL=8065907396593.657.1594058218758;SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWfNmU2el7DUf_geNmOZpOA5JpX5KMhUgL.FoqNSozpe0eNSo.2dJLoIEMpehnLxK-L12qLBoMLxKqL1heL1h-LxKqL122LBK-LxKMLB-zL1K.t;UOR=,,www.google.com;SCF=AneetFdEbQdzrPjcqxzupwzJub5dDpiV3Eq2fxW6pccnLRYosB45KSy9EYTbSNH3kqtaMfOxBwwCQ9PHGqvnENc.;SUB=_2A25y50n6DeRhGeBJ7VAQ8y3LzTWIHXVRlTwyrDV8PUNbmtAKLUrZkW9NRisOaqBueTghfUIv0irT7obalu2kggHJ;SRT=D.QqHBJZPuiOBli!Mb4cYGS4H1isSB4OYuJrowPEWHNEYd4co4W3bpMERt4EPKRcsrA4kJPQHTTsVuObH9VryYV4HsSPMeTFSPNQRnTQH4i-y8RdosJckeSrY1*B.vAflW-P9Rc0lR-yk!DvnJqiQVbiRVPBtS!r3JZPQVqbgVdWiMZ4siOzu4DbmKPWQS!ukPOHbUDWmPdSaS-PKU4yTVmyBi49ndDPIOdYPSrnlMcyoObi65eBbJFPK4-0lJcM1OFyHM4snSdYlOGYII4noJeEJAcyiOGYIV4noTFPJJdXkOGYIV4noJZHJA!jkOGYIO4oCIQ9JJ4jlWv77;SRF=1608726954;ALF=1640262954;SSOLoginState=1608726954;wvr=6;_s_tentry=-;Apache=9461767221891.807.1608732941708;ULV=1608732941780:19:3:1:9461767221891.807.1608732941708:1607756438564;wb_view_log_6762133769=1920*10801;webim_unReadCount=%7B%22time%22%3A1608739784663%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A10%2C%22msgbox%22%3A0%7D;";
        String[] cooks = cookiesStr.split(";");
        for (String cs : cooks) {
            if (cs.indexOf("=") != -1) {
                String key = cs.split("=")[0].trim();
                String val = cs.split("=")[1].trim();
                cookies.put(key, val);
            }
        }
        return cookies;
    }
    
    private void startSpider() throws Exception {
        String userUrl = "https://weibo.com/u/5857314727";
        Map<String, String> cookiesMap = getCookies();
        String jointStr = getPageInfo(cookiesMap, userUrl);
//        if (cookiesMap == null || cookiesMap.isEmpty()) {
//            cookiesMap = cmsCookiesUtilsController.getWeiboCookies();
//        }
        long lastVisitWeiboTime = 0;
        int page = 1;
//        while (page <= pageNum) {
            logger.info("//************* 开始爬取第" + page + "页 ***********//");
            for (int i = 0; i < 3; i++) {
                String url = "";
                if (i == 0) {
                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + (page - 1) + "&pagebar=" + 0;
                }
                if (i == 1) {
                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + page + "&pagebar=" + 0;
                }
                if (i == 2) {
                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + page + "&pagebar=" + page;
                }
                //System.out.println("  ------ 爬取的url为：" + url + " -------");
                Connection conn = ConnectionUtil.getConnection(url).cookies(cookiesMap);
                conn.get();
                String html = conn.response().body();
                System.out.println(html);
                JSONObject jsonObj = new JSONObject(html);
                if (jsonObj.has("data")) {
                    String data = jsonObj.getString("data");
                    Document dataDoc = Jsoup.parse(data);
                    Elements midEls = dataDoc.select("div[mid].WB_cardwrap"); //微博id的内容列表
                    for (Element midEl : midEls) { //循环每一条微博
                        Elements detailAEls = midEl.select("div.WB_detail").select("div.WB_from").select("a");
                        Element detailAEl = detailAEls.get(0);
                        String detailHref = detailAEl.attr("href");
                        String weiboDetailDate = detailAEl.attr("title");
//                        Date detailDate = DateUtil.getYYYYMMddHHmm(weiboDetailDate);
//                        long detailDateLong = detailDate.getTime();
//                        if (lastVisitWeiboTime == 0) {
//                            lastVisitWeiboTime = detailDateLong;
//                        }
//                        if (isCheckLastTime && detailDateLong <= hasLastVisitWeiboTime) continue;
                        String mid = midEl.attr("mid"); //微博id
//                        if (isnew) {
//                            Query query = new Query();
//                            query.addCriteria(Criteria.where("weiboId").is(mid));
//                            WeiboComment hasWeiboComment = mongoTemplate.findOne(query, WeiboComment.class);
//                            if (hasWeiboComment != null && hasWeiboComment.getId() != null) continue; //暂时仅爬取最新的微博，后续添加再次爬取参数
//                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        Element WB_info_el = midEl.select("div.WB_info").get(0); //发微博用户的信息
                        Element usercardEl = WB_info_el.select("a[usercard]").get(0); //用户的链接
                        String usercard = usercardEl.attr("usercard");
                        String weiboUid = null;
                        if (usercard.indexOf("id=") != -1) {
                            String usercardTmp = usercard.split("id=")[1];
                            weiboUid = usercardTmp.substring(0, usercardTmp.indexOf("&"));
                        }
                        String weiboUname = usercardEl.text();
                        Elements feed_list_content_els = midEl.select("div[node-type=feed_list_content]"); //每一条微博的内容element
                        for (Element feed_list_content_el : feed_list_content_els) { //循环每一条微博
                            boolean longContentSave = false;
                            Integer other_id = null;
                            String longContentText = null, doubanUrl = null;
                            Elements WB_text_opt_els = feed_list_content_el.select("a.WB_text_opt"); //判断是否有长微博被隐藏
                            if (WB_text_opt_els != null && WB_text_opt_els.size() > 0) {
                                Element WB_text_opt_el = feed_list_content_el.select("a.WB_text_opt").get(0);
                                String WB_text_opt_html = WB_text_opt_el.html();
                                if (WB_text_opt_html.indexOf("展开全文") != -1) { //存在展开全文的链接
                                    longContentText = longContent(cookiesMap, mid);
                                    System.out.println(longContentText);
                                }
                            } else {
                                System.out.println(feed_list_content_el.html());
                            }
                        }
                    }
                } else {
                    break;
                }
            }
            logger.info("//************* 第" + page + "页爬取结束 ***********//");
            page++;
//        }
    }
    
//    @Test
//    public void startSpider() {
//        int pageNum = 1;
//        Map<String, String> cookiesMap = cmsCookiesUtilsController.getWeiboCookies();
//        boolean isnew = false;
//        String userUrl = "https://weibo.com/u/1617566333";
//        
//        boolean fhtsksFlag = false; //凤凰天使特殊处理
//        if (userUrl.indexOf("weibo.com/fhtsks") != -1) {
//            fhtsksFlag = true;
//        }
//        String jointStr = getPageInfo(cookiesMap, userUrl);
//        if (cookiesMap == null || cookiesMap.isEmpty()) {
//            cookiesMap = cmsCookiesUtilsController.getWeiboCookies();
//        }
//        int page = 1;
//        while (page <= pageNum) {
//            System.out.println("//************* 开始爬取第" + page + "页 ***********//");
//            for (int i = 0; i < 3; i++) {
//                String url = "";
//                if (i == 0) {
//                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + (page - 1) + "&pagebar=" + 0;
//                }
//                if (i == 1) {
//                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + page + "&pagebar=" + 0;
//                }
//                if (i == 2) {
//                    url = mblogUrl + jointStr + "&page=" + page + "&pre_page=" + page + "&pagebar=" + page;
//                }
//                //System.out.println("  ------ 爬取的url为：" + url + " -------");
//                Connection conn = ConnectionUtil.getConnection(url).cookies(cookiesMap);
//                try {
//                    conn.get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String html = conn.response().body();
//                JSONObject jsonObj = new JSONObject(html);
//                if (jsonObj.has("data")) {
//                    String data = jsonObj.getString("data");
//                    Document dataDoc = Jsoup.parse(data);
//                    Elements midEls = dataDoc.select("div[mid].WB_cardwrap"); //微博id的内容列表
//                    for (Element midEl : midEls) { //循环每一条微博
//                        Elements detailAEls = midEl.select("div.WB_detail").select("div.WB_from").select("a");
//                        Element detailAEl = detailAEls.get(0);
//                        String detailHref = detailAEl.attr("href");
//                        String weiboDetailDate = detailAEl.attr("title");
//                        Date detailDate = DateUtil.getYYYYMMddHHmm(weiboDetailDate);
//                        
//                        String mid = midEl.attr("mid"); //微博id
//                        if (mid.equals("4419033707774666"))
//                            System.out.println("===============");
//                        else continue;
//                        if (isnew) {
//                            Query query = new Query();
//                            query.addCriteria(Criteria.where("weiboId").is(mid));
//                            WeiboComment hasWeiboComment = mongoTemplate.findOne(query, WeiboComment.class);
//                            if (hasWeiboComment != null && hasWeiboComment.getId() != null) continue; //暂时仅爬取最新的微博，后续添加再次爬取参数
//                        }
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        
//                        Element WB_info_el = midEl.select("div.WB_info").get(0); //发微博用户的信息
//                        Element usercardEl = WB_info_el.select("a[usercard]").get(0); //用户的链接
//                        String usercard = usercardEl.attr("usercard");
//                        String weiboUid = null;
//                        if (usercard.indexOf("id=") != -1) {
//                            String usercardTmp = usercard.split("id=")[1];
//                            weiboUid = usercardTmp.substring(0, usercardTmp.indexOf("&"));
//                        }
//                        String weiboUname = usercardEl.text();
//                        Elements feed_list_content_els = midEl.select("div[node-type=feed_list_content]"); //每一条微博的内容element
//                        for (Element feed_list_content_el : feed_list_content_els) { //循环每一条微博
//                            boolean longContentSave = false;
//                            Integer other_id = null;
//                            String longContentText = null, doubanUrl = null;
//                            Elements WB_text_opt_els = feed_list_content_el.select("a.WB_text_opt"); //判断是否有长微博被隐藏
//                            if (WB_text_opt_els != null && WB_text_opt_els.size() > 0) {
//                                Element WB_text_opt_el = feed_list_content_el.select("a.WB_text_opt").get(0);
//                                String WB_text_opt_html = WB_text_opt_el.html();
//                                if (WB_text_opt_html.indexOf("展开全文") != -1) { //存在展开全文的链接
//                                    Map<String, Object> resultMap = longContent(cookiesMap, mid);
//                                    longContentText = resultMap.get("longContentText") != null ? resultMap.get("longContentText").toString() : null; //查看全文信息
//                                    if (CommonUtil.mapIsNotBlank(resultMap, "otherId")) {
//                                        other_id = Integer.parseInt(resultMap.get("otherId").toString());
//                                    }
//                                    if (CommonUtil.mapIsNotBlank(resultMap, "doubanUrl")) {
//                                        doubanUrl = resultMap.get("doubanUrl").toString();
//                                    }
//                                    longContentSave = true;
//                                }
//                            } else {
//                                Map<String, Object> resultMap = ifHasUrlToReturnText(feed_list_content_el);
//                                longContentText = resultMap.get("longContentText") != null ? resultMap.get("longContentText").toString() : null; //查看全文信息
//                                if (CommonUtil.mapIsNotBlank(resultMap, "otherId")) {
//                                    other_id = Integer.parseInt(resultMap.get("otherId").toString());
//                                }
//                                if (CommonUtil.mapIsNotBlank(resultMap, "doubanUrl")) {
//                                    doubanUrl = resultMap.get("doubanUrl").toString();
//                                }
//                            }
//                            if (StringUtils.isNotBlank(longContentText)) {
//                                System.out.println("  **" + longContentText);
//                                WeiboComment weiboComment = new WeiboComment();
//                                weiboComment.setCommentText(longContentText);
//                                weiboComment.setCreate_time(new Date());
//                                weiboComment.setIsAdd(0);
//                                weiboComment.setUpdate_time(new Date());
//                                weiboComment.setUserStr(userUrl);
//                                weiboComment.setWeiboId(mid);
//                                weiboComment.setWeiboUid(weiboUid);
//                                weiboComment.setWeiboUname(weiboUname);
//                                weiboComment.setWeiboDetailUrl(detailHref);
//                                weiboComment.setWeiboDetailTime(detailDate);
//                                weiboComment.setOtherId(other_id);
//                                weiboComment.setDoubanUrl(doubanUrl);
//                                if (fhtsksFlag) {
//                                    if (longContentText.indexOf("韩影") != 1)
//                                        saveWeiboComment(weiboComment);
//                                } else
//                                    saveWeiboComment(weiboComment);
//                                System.out.println("--------------------------");
//                            } else {
//                                if (longContentSave) continue; //展开全文操作中已爬取评论中的链接，所以在此不再爬取评论
//                                List<String> commentTextList = urlFromComment(cookiesMap, mid);
//                                if (commentTextList != null && commentTextList.size() > 0) {
//                                    StringBuffer commentBuffer = new StringBuffer();
//                                    System.out.println("  **" + feed_list_content_el.text());
//                                    commentBuffer.append(feed_list_content_el.text());
//                                    for (String commentText : commentTextList) {
//                                        if (StringUtils.isNotBlank(commentText))
//                                            commentBuffer.append("<br>").append(commentText);
//                                    }
//                                    System.out.println("  **" + commentBuffer.toString());
//                                    WeiboComment weiboComment = new WeiboComment();
//                                    weiboComment.setCommentText(commentBuffer.toString());
//                                    weiboComment.setCreate_time(new Date());
//                                    weiboComment.setIsAdd(0);
//                                    weiboComment.setUpdate_time(new Date());
//                                    weiboComment.setUserStr(userUrl);
//                                    weiboComment.setWeiboId(mid);
//                                    weiboComment.setWeiboUid(weiboUid);
//                                    weiboComment.setWeiboUname(weiboUname);
//                                    weiboComment.setWeiboDetailUrl(detailHref);
//                                    weiboComment.setWeiboDetailTime(detailDate);
//                                    weiboComment.setOtherId(other_id);
//                                    weiboComment.setDoubanUrl(doubanUrl);
//                                    if (fhtsksFlag) {
//                                        if (commentBuffer.toString().indexOf("韩影") != 1)
//                                            saveWeiboComment(weiboComment);
//                                    } else
//                                        saveWeiboComment(weiboComment);
//                                    System.out.println("--------------------------");
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    break;
//                }
//            }
//            System.out.println("//************* 第" + page + "页爬取结束 ***********//");
//            page++;
//        }
//    }
    
    
    
    
    
    /**
     * 保存和更新微博内容
     * @param weiboComment
     */
/*    private void saveWeiboComment(WeiboComment weiboComment) {
        Query weiboIdQuery = new Query(Criteria.where("weiboId").is(weiboComment.getWeiboId()));
        //Query weiboUidQuery = new Query(Criteria.where("weiboUid").is(weiboComment.getWeiboUid()));
        WeiboComment hasWeiboComment = mongoTemplate.findOne(weiboIdQuery, WeiboComment.class);
        if (hasWeiboComment != null) {
            if (hasWeiboComment.getIsAdd() == 0) {
                Update update = new Update();
                update.set("update_time", new Date());
                update.set("commentText", weiboComment.getCommentText());
                mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(hasWeiboComment.getId())), update, WeiboComment.class);
            }
        } else mongoTemplate.save(weiboComment);
    }*/
    
    String cookiesStr = "";
    /*自动登录微博，并返回cookies*/
/*    private Map<String, String> autoLoginWeibo() throws Exception {
        String username = weiboLoginUserName;
        username = URLEncoder.encode(username, "utf-8");
        String base64Username = Base64.getEncoder().encodeToString(username.getBytes());
        String preLoginUrl = "https://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&"
                + "su=" + base64Username + "&rsakt=mod&checkpin=0&client=ssologin.js(v1.4.19)&_=" + System.currentTimeMillis();
        Map<String, Object> proxyMap = CmsProxyUtils.getProxy("https://weibo.com");
        String ip = proxyMap.get("ip").toString();
        Integer port = Integer.parseInt(proxyMap.get("port").toString());
        Document doc = ConnectionUtil.getConnection(preLoginUrl).header("Referer", "https://www.weibo.com/").proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port))).get();
        String bodyHtml = doc.body().html();
        //System.out.println(bodyHtml);
        //System.out.println("========================  " + preLoginUrl);
        String bHtml = bodyHtml.split("sinaSSOController.preloginCallBack\\(")[1];
        bHtml = bHtml.substring(0, bHtml.lastIndexOf(")"));
        JSONObject jsonObj = new JSONObject(bHtml);
        String pubkey = jsonObj.get("pubkey") + "";
        String servertime = jsonObj.get("servertime") + "";
        String nonce = jsonObj.get("nonce") + "";
        String rsakv = jsonObj.get("rsakv") + "";
        
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("entry", "weibo");
        dataMap.put("gateway", "1");
        dataMap.put("from", "");
        dataMap.put("savestate", "7");
        dataMap.put("qrcode_flag", "false");
        dataMap.put("useticket", "1");
        dataMap.put("pagerefer", "https://www.weibo.com/?topnav=1&mod=logo");
        dataMap.put("vsnf", "1");
        dataMap.put("su", base64Username);
        dataMap.put("service", "miniblog");
        dataMap.put("servertime", servertime);
        dataMap.put("nonce", nonce);
        dataMap.put("pwencode", "rsa2");
        dataMap.put("rsakv", rsakv);
        
        dataMap.put("sr", (1366*768) + "");
        dataMap.put("encoding", "UTF-8");
        dataMap.put("prelt", "25");
        dataMap.put("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
        dataMap.put("returntype", "META");
        
        String message = servertime + "\t" + nonce + "\n" + weiboLoginPassword;
        try {
            dataMap.put("sp", rsa(pubkey, "10001", message));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | InvalidKeySpecException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        //System.out.println(dataMap);
        Connection conn = ConnectionUtil.getConnection("https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.19)")
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
        conn.data(dataMap);
        //Document loginResultDoc = conn.post();
        //System.out.println(loginResultDoc.html());
        Map<String, String> cookiesMap = conn.response().cookies();
        cookiesStr = "";
        cookiesMap.forEach((k, v) -> {
            cookiesStr = cookiesStr + k + "=" + v + ";";
        });
        //System.out.println(cookiesStr);
        if (cookiesMap != null && !cookiesMap.isEmpty()) {
            cmsCookiesUtilsController.updateCookiesUtils(cookiesStr, weiboCookiesMapKey);
        }
        return cookiesMap;
    }*/
    
    public static void main(String args[]) throws Exception {
        new WeiboUtilsController().startSpider();
    }
    
    public static String rsa(String pubkey, String exponentHex, String pwd)
            throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        BigInteger m = new BigInteger(pubkey, 16);
        BigInteger e = new BigInteger(exponentHex, 16);
        RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);

        // 创建公钥
        RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
        Cipher enc = Cipher.getInstance("RSA");
        enc.init(Cipher.ENCRYPT_MODE, pub);

        byte[] encryptedContentKey = enc.doFinal(pwd.getBytes("UTF-8"));

        return new String(encodeHex(encryptedContentKey));
    }

    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };

    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }
}
