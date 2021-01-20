package com.tools.weibo.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tools.cms.cookies.CmsCookiesUtilsController;
import com.tools.common.ListPage;
import com.tools.model.WeiboUser;
import com.tools.utils.ConnectionUtil;
import com.tools.utils.DateUtils;

@Component
public class WeiboUtilsController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private CmsCookiesUtilsController cmsCookiesUtilsController;
    
    private static String mblogUrl = "https://weibo.com/p/aj/v6/mblog/mbloglist?ajwvr=6"
            + "&profile_ftype=1&is_all=1&pl_name=&script_uri=&feed_type=0&__rnd=";
    private static String longContentUrl = "https://weibo.com/p/aj/mblog/getlongtext?ajwvr=6&is_settop&is_sethot&is_setfanstop&is_setyoudao&__rnd="; //得到微博长内容的链接
    //private static String commentUrl = "https://weibo.com/aj/v6/comment/big?ajwvr=6&from=singleWeiBo&__rnd=";
    //private static final String client_ID = "2231125779";
    
    private Logger logger = LoggerFactory.getLogger(WeiboUtilsController.class);
    
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
    
    private Map<String, String> getCookies() {
        /*Map<String, String> cookies = Maps.newHashMap();
        String cookiesStr = "tid=SwrjKODsEYoK/Lgk0bDwu5y1jxGxucecRJZGdrKQhkU=__095;SINAGLOBAL=7215125845365.442.1587363248600;_ga=GA1.2.1285144861.1600155460;ULV=1607326709755:21:1:1:9039805117907.8.1607326709692:1605578565066;UOR=news.ifeng.com,widget.weibo.com,login.sina.com.cn;SCF=AhvVSaDk77uTV8ztYY33KbjXX246ihp-lkdJVKwaRUwlTeYZ9V5Gz3yWTK9L6ZviLEohz8Scz1CfAtjPzSwAuGc.;SUB=_2A25y7rgxDeRhGeBJ7VAQ8y3LzTWIHXVRna75rDV8PUJbmtAKLRj6kW9NRisOamGgNFbgxagAT1OUIrISs7T18uXq;SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WWfNmU2el7DUf_geNmOZpOA5JpX5K-hUgL.FoqNSozpe0eNSo.2dJLoIEMpehnLxK-L12qLBoMLxKqL1heL1h-LxKqL122LBK-LxKMLB-zL1K.t;SRT=D.QqHBJZPui3He5mMb4cYGS4H1isSB4OYuJrowPEWHNEYd4cuYiqPpMERt4EP1RcsrA4kJ4cf-TsVuObH9VryYU4WeO!S8S3YYSrE4J4yPNQHH43J3PDmtWPYo*B.vAflW-P9Rc0lR-yk!DvnJqiQVbiRVPBtS!r3JZPQVqbgVdWiMZ4siOzu4DbmKPWQS!ukPOHbUDWmPdSaS-PKU4yTVmyBi49ndDPIIdYPSrnlMcyoObi65eBbJFPK4-0lJcM1OFyHM4snSdYlOGYII4noJeEJAcyiOGYIV4noTFPJJdXkOGYIV4noJZHJA!jkOGYIO4oCIQ9JJ4jlWv77;SRF=1609222241;ALF=1639820326;SSOLoginState=1609222241;wvr=6;wb_view_log_6762133769=1366*7681;";
        String[] cooks = cookiesStr.split(";");
        for (String cs : cooks) {
            if (cs.indexOf("=") != -1) {
                String key = cs.split("=")[0].trim();
                String val = cs.split("=")[1].trim();
                cookies.put(key, val);
            }
        }*/
        Map<String, String> cookies = cmsCookiesUtilsController.getWeiboCookies();
        return cookies;
    }
    
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
    
    
    /**
     * 获得所有有资源的微博用户名称或id列表
     * @param modelMap
     * @return
     */
    @RequestMapping("/getWeiboUserList")
    public String getWeiboUserList(HttpServletRequest request, ModelMap modelMap) {
        /*String orderCol = request.getParameter("orderCol");
        String order = request.getParameter("order");*/
        Query query = new Query();
        int p = request.getParameter("p") != null ? Integer.parseInt(request.getParameter("p")) - 1 : 0;
        int firstRow = p * 30;
        int rowNum = 30;
        Sort sort = Sort.by(Direction.DESC, "update_time");
        Pageable pageable = PageRequest.of(p, 30, sort);
        
        long allRow = mongoTemplate.count(query, WeiboUser.class);
        List<WeiboUser> weiboUserList = mongoTemplate.find(query.with(pageable), WeiboUser.class);
        ListPage<WeiboUser> listPage = new ListPage<WeiboUser>(weiboUserList, firstRow, rowNum,
                new Long(allRow).intValue());
        modelMap.addAttribute("weiboUserListPage", listPage);
        modelMap.addAttribute("p", p);
        modelMap.addAttribute("rowNum", rowNum);
        return "/cms/weibo/getWeiboUserList";
    }
    
    /**
     * 获得所有需要爬取的微博用户名称或id列表
     * @param modelMap
     * @return
     */
    @RequestMapping("/getWeiboUserSpiderList")
    public String getWeiboUserSpiderList(ModelMap modelMap) {
        return "/cms/weibo/getWeiboUserSpiderList";
    }
    
    /**
     * 保存有资源的微博用户名称或id
     */
    @RequestMapping("/saveWeiboUser")
    public String saveWeiboUser(String userStr, String userUrl, String batchUserStr) {
        if (StringUtils.isNotBlank(batchUserStr)) {
            String[] userStrs = batchUserStr.split("\r\n");
            
            Map<String, String> userMap = new HashMap<String, String>();
            String key = null;
            for (String uStr : userStrs) {
                if (uStr.indexOf("http") != -1 || uStr.indexOf("https") != -1) {
                    //System.out.println(key + "   " + uStr);
                    userMap.put(key, uStr);
                } else {
                    key = uStr;
                }
            }
            
            userMap.forEach((k, v) -> {
                saveWeiboUserOp(k, v);
            });
            return "redirect:/cms/weibo/getWeiboUserList";
        }
        if (StringUtils.isNotBlank(userStr) && StringUtils.isNotBlank(userUrl)) {
            saveWeiboUserOp(userStr, userUrl);
            return "redirect:/cms/weibo/getWeiboUserList";
        }
        
        return "/cms/weibo/saveWeiboUser";
    }
    
    private void saveWeiboUserOp(String userStr, String userUrl) {
        WeiboUser hasWeiboUser = mongoTemplate.findOne(new Query(Criteria.where("userUrl").is(userUrl)), WeiboUser.class);
        if (hasWeiboUser != null) {
            Update update = new Update();
            update.set("userStr", userStr);
            update.set("userUrl", userUrl);
            update.set("update_time", new Date());
            mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(hasWeiboUser.getId())), update, WeiboUser.class);
        } else {
            WeiboUser weiboUsercls = new WeiboUser();
            weiboUsercls.setCreate_time(new Date());
            weiboUsercls.setIsSpider(1);
            weiboUsercls.setStatus(1);
            weiboUsercls.setUpdate_time(new Date());
            weiboUsercls.setUserStr(userStr);
            weiboUsercls.setUserUrl(userUrl);
            mongoTemplate.save(weiboUsercls);
        }
    }
    
    /**
     * 保存需要爬取的微博用户
     * @param weiboUser
     */
    @RequestMapping("/saveWeiboUserSpider")
    public String saveWeiboUserSpider(String weiboUser) {
        return "/cms/weibo/saveWeiboUserSpider";
    }
    
    /**
     * 爬取微博用户内容
     * @throws Exception
     */
    @RequestMapping("/spider")
    @ResponseBody
    public String startSpider(HttpServletRequest request) throws Exception {
        int pageNum = StringUtils.isNotBlank(request.getParameter("pageNum")) ? Integer.parseInt(request.getParameter("pageNum")) : 100;
        String userUrl = "https://weibo.com/u/5857314727";
        Map<String, String> cookiesMap = getCookies();
        String jointStr = getPageInfo(cookiesMap, userUrl);
//        if (cookiesMap == null || cookiesMap.isEmpty()) {
//            cookiesMap = cmsCookiesUtilsController.getWeiboCookies();
//        }
        long lastVisitWeiboTime = 0;
        int page = 1;
        while (page <= pageNum) {
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
                if (!html.startsWith("{")) { //没有cookies或cookies已过期
                    return "noCookies";
                }
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
                        Date detailDate = DateUtils.convertStringToDate(DateUtils.longminutePattern, weiboDetailDate);
                        long detailDateLong = detailDate.getTime();
                        if (lastVisitWeiboTime == 0) {
                            lastVisitWeiboTime = detailDateLong;
                        }
                        //if (isCheckLastTime && detailDateLong <= hasLastVisitWeiboTime) continue;
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
                            Map<String, Object> weiboMap = Maps.newHashMap();
                            weiboMap.put("weiboId", mid); // 微博内容id
                            weiboMap.put("weiboUid", weiboUid);
                            weiboMap.put("weiboUname", weiboUname);
                            long now = System.currentTimeMillis();
                            weiboMap.put("createTime", now);
                            weiboMap.put("updateTime", now);
                            weiboMap.put("weiboDetailUrl", detailHref);
                            weiboMap.put("weiboDetailTime", detailDateLong);
                            String longContentText = null;
                            Elements WB_media_a = feed_list_content_el.parent().select("ul.WB_media_a");
                            if (WB_media_a != null) {
                                String action_data = WB_media_a.attr("action-data");
                                //System.out.println("================  " + action_data);
                                List<String> largeImageList = Lists.newArrayList();
                                if (StringUtils.isNotBlank(action_data)) {
                                    String[] action_datas = action_data.split("&");
                                    for (String adata : action_datas) {
                                        if (adata.indexOf("clear_picSrc=") != -1) { //查找出所有的大图链接
                                            adata = adata.replace("clear_picSrc=", "");
                                            String[] adataArr = adata.split(",");
                                            for (String ad : adataArr) {
                                                largeImageList.add(URLDecoder.decode(ad, "utf-8"));
                                            }
                                        }
                                    }
                                }
                                weiboMap.put("largeImageList", largeImageList);
                                Elements WB_media_a_lis = WB_media_a.select("li");
                                List<String> smallImageList = Lists.newArrayList();
                                List<String> gifList = Lists.newArrayList();
                                List<String> videoList = Lists.newArrayList();
                                for (Element WB_media_a_li : WB_media_a_lis) {
                                    if (WB_media_a_li.hasClass("WB_video")) { //视频
                                        String video_action_data = WB_media_a_li.attr("action-data");
                                        if (StringUtils.isNotBlank(video_action_data)) {
                                            logger.info("================  " + video_action_data);
                                            String[] action_datas = video_action_data.split("&");
                                            for (String adata : action_datas) {
                                                if (adata.indexOf("video_src") != -1) {
                                                    adata = adata.replace("video_src=", "");
                                                    String[] adataArr = adata.split(",");
                                                    for (String ad : adataArr) {
                                                        videoList.add(URLDecoder.decode(ad, "utf-8"));
                                                    }
                                                }
                                            }
                                        }
                                    } else if (WB_media_a_li.hasClass("WB_pic")) { //图片
                                        String gif_action_data = WB_media_a_li.attr("action-data");
                                        if (StringUtils.isNotBlank(gif_action_data)) {
                                            logger.info("================  " + gif_action_data);
                                            String[] action_datas = gif_action_data.split("&");
                                            for (String adata : action_datas) {
                                                if (adata.indexOf("gif_url") != -1) {
                                                    adata = adata.replace("gif_url=", "");
                                                    String[] adataArr = adata.split(",");
                                                    for (String ad : adataArr) {
                                                        gifList.add(URLDecoder.decode(ad, "utf-8"));
                                                    }
                                                }
                                            }
                                        }
                                        
                                        Elements WB_gif_boxs = WB_media_a_li.select("div.WB_gif_box");
                                        if (WB_gif_boxs != null && WB_gif_boxs.size() > 0) {
                                            for (Element WB_gif_box : WB_gif_boxs) {
                                                if (WB_gif_box.select("img") != null && WB_gif_box.select("img").size() > 0) {
                                                    Element gif_img = WB_gif_box.select("img").get(0);
                                                    smallImageList.add(gif_img.attr("src"));
                                                }
                                            }
                                        } else {
                                            Elements imgEls = WB_media_a_li.select("img");
                                            for (Element imgEl : imgEls) {
                                                smallImageList.add(imgEl.attr("src"));
                                            }
                                        }
                                    }
                                }
                                weiboMap.put("videoList", videoList);
                                weiboMap.put("gifList", gifList);
                                weiboMap.put("smallImageList", smallImageList); //内容小图
                            }
                            Elements WB_text_opt_els = feed_list_content_el.select("a.WB_text_opt"); //判断是否有长微博被隐藏
                            if (WB_text_opt_els != null && WB_text_opt_els.size() > 0) {
                                Element WB_text_opt_el = feed_list_content_el.select("a.WB_text_opt").get(0);
                                String WB_text_opt_html = WB_text_opt_el.html();
                                if (WB_text_opt_html.indexOf("展开全文") != -1) { //存在展开全文的链接
                                    longContentText = longContent(cookiesMap, mid);
                                    logger.info(longContentText);
                                }
                            } else {
                                weiboMap.put("content", feed_list_content_el.html());
                                logger.info(feed_list_content_el.html());
                            }
                            logger.info("===========================  ");
                            logger.info(weiboMap.get("weiboId").toString());
                            logger.info(weiboMap.get("content").toString());
                            Map<String, Object> resultMap = mongoTemplate.save(weiboMap, "weibo_content");
                            
                            System.out.println(resultMap);
                            /*if (weiboMap.containsKey("largeImageList")) {
                                List<String> largeImageList = (List<String>) weiboMap.get("largeImageList");
                                for (String largeImage : largeImageList) {
                                    System.out.println("largeImage：" + largeImage);
                                }
                            }
                            if (weiboMap.containsKey("videoList")) {
                                List<String> videoList = (List<String>) weiboMap.get("videoList");
                                for (String video : videoList) {
                                    System.out.println("video：" + video);
                                }
                            }
                            if (weiboMap.containsKey("gifList")) {
                                List<String> gifList = (List<String>) weiboMap.get("gifList");
                                for (String gif : gifList) {
                                    System.out.println("gif：" + gif);
                                }
                            }
                            if (weiboMap.containsKey("smallImageList")) {
                                List<String> smallImageList = (List<String>) weiboMap.get("smallImageList");
                                for (String smallImage : smallImageList) {
                                    System.out.println("smallImage：" + smallImage);
                                }
                            }*/
                            logger.info("===========================  ");
                        }
                    }
                } else {
                    break;
                }
            }
            logger.info("//************* 第" + page + "页爬取结束 ***********//");
            page++;
        }
        return "success";
    }
    
    /**
     * 跳转到微博爬取页面
     * @return
     */
    @RequestMapping("/spiderWeiboIndex")
    public String spiderWeiboIndex() {
        return "/cms/weibo/spiderWeibo";
    }
}
