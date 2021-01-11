package com.tools.cms.cookies;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.tools.utils.DateUtils;
import com.tools.utils.DoubanMovieUtil;

@Controller
@RequestMapping("/cms/cookiesUtils")
public class CmsCookiesUtilsController {
    Logger logger = LoggerFactory.getLogger(CmsCookiesUtilsController.class);
    private static final String doubanCookiesMapKey = "doubanCookies";
    private static final String rarbgCookiesMapKey = "rarbgCookies";
    private static final String zimuzuCookiesMapKey = "zimuzuCookies";
    private static final String torrCookiesMapKey = "torrCookies";
    private static final String weiboCookiesMapKey = "weiboCookies";
    private static final String cookiesTable = "cookieInfoMongo";
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 更新豆瓣cookies
     * @param request
     * @param response
     * @param modelMap
     * @param cookiesStr
     * @return
     */
    @RequestMapping("/updateDoubanCookies")
    public String updateDoubanCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String cookiesStr = request.getParameter("cookiesStr");
        String ck = request.getParameter("ck");
        String skind = request.getParameter("skind");
        String addItemUrl = request.getParameter("addItemUrl");
        
        JSONObject jsonObj = new JSONObject();
        if (StringUtils.isNotBlank(cookiesStr)) {
            jsonObj.put("cookies", cookiesStr);
            Map<String, String> cookies = new HashMap<String, String>();
            String[] cooks = cookiesStr.split(";");
            for (String cs : cooks) {
                if (cs.indexOf("=") != -1) {
                    String key = cs.split("=")[0].trim();
                    String val = cs.split("=")[1].trim();
                    cookies.put(key, val);
                }
            }
            DoubanMovieUtil.setCookies(cookies);
        }
        if (StringUtils.isNotBlank(ck)) {
            jsonObj.put("ck", ck);
        }
        if (StringUtils.isNotBlank(skind)) {
            jsonObj.put("skind", skind);
        }
        if (StringUtils.isNotBlank(addItemUrl)) {
            jsonObj.put("addItemUrl", addItemUrl);
        }
        
        Query query = new Query(Criteria.where("cookiesName").is(doubanCookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, String> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            if (!jsonObj.toMap().isEmpty()) {
                Update update = new Update();
                update.set("cookiesJson", jsonObj.toString());
                mongoTemplate.updateFirst(query, update, cookiesTable);
            } else {
                String cookiesJson = findCookiesMap.get("cookiesJson");
                jsonObj = new JSONObject(cookiesJson);
                cookiesStr = jsonObj.get("cookies") + "";
                ck = jsonObj.get("ck") + "";
                skind = jsonObj.has("skind") ? jsonObj.get("skind") + "" : "";
                addItemUrl = jsonObj.get("addItemUrl") + "";
            }
            
        } else {
            Map<String, String> saveMap = new HashMap<String, String>();
            saveMap.put("cookiesName", doubanCookiesMapKey);
            saveMap.put("cookiesJson", jsonObj.toString());
            mongoTemplate.save(saveMap, cookiesTable);
        }
        
        modelMap.addAttribute("cookiesStr", cookiesStr);
        modelMap.addAttribute("ck", ck);
        modelMap.addAttribute("skind", skind);
        modelMap.addAttribute("addItemUrl", addItemUrl);
        return "/cms/cookies/updateDoubanCookies";
    }

    /**
     * 更新rarbg's cookies
     * @param request
     * @param response
     * @param modelMap
     * @param cookiesStr
     * @return
     */
    @RequestMapping("/updateRarbgCookies")
    public String updateRarbgCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
        updateCookiesUtils(cookiesStr, rarbgCookiesMapKey);
        if (StringUtils.isEmpty(cookiesStr))
            modelMap.addAttribute("rarbgCookiesMap", getCookiesStringMap(rarbgCookiesMapKey));
        return "/cms/cookies/updateRarbgCookies";
    }
    
    /**
     * 通过chrome的js自动更新rargb的cookies
     * @param request
     * @param response
     * @param modelMap
     * @param cookiesStr
     */
    @ResponseBody
    @RequestMapping("/ajaxUpdateRarbgCookies")
    public String ajaxUpdateRarbgCookies(HttpServletRequest request) {
        updateCookiesUtils(request.getParameter("cookiesStr"), rarbgCookiesMapKey);
        return "success";
    }

    /**
     * 更新zimuzu's cookies，人人影视
     * @param request
     * @param response
     * @param modelMap
     * @param cookiesStr
     * @return
     */
    @RequestMapping("/updateZimuzuCookies")
    public String updateZimuzuCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
        updateCookiesUtils(cookiesStr, zimuzuCookiesMapKey);
        if (StringUtils.isEmpty(cookiesStr))
            modelMap.addAttribute("zimuzuCookiesMap", getCookiesStringMap(zimuzuCookiesMapKey));
        return "/cms/cookies/updateZimuzuCookies";
    }

    @RequestMapping("/updateAliCookies")
    public String updateAliCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
//        if (StringUtils.isNotBlank(cookiesStr)) {
//            try {
//                String filePath = FileUtil.getAbsolutePath();
//                File rarbgCookiesFile = new File(filePath + File.separator + "ali_cookies.txt");
//                FileWriter fileWriter = new FileWriter(rarbgCookiesFile);
//                fileWriter.write(cookiesStr);
//                fileWriter.flush();
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                logger.error(e.toString());
//            }
//
//        }
        return "/cms/cookies/updateAliCookies";
    }
    
    @RequestMapping("/updateWeiboCookies")
    public String updateWeiboCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
        updateCookiesUtils(cookiesStr, weiboCookiesMapKey);
        if (StringUtils.isEmpty(cookiesStr))
            modelMap.addAttribute("weiboCookiesMap", getCookiesStringMap(weiboCookiesMapKey));
        
        return "/cms/cookies/updateWeiboCookies";
    }
    
    @ResponseBody
    @RequestMapping("/ajaxUpdateWeiboCookies")
    public void ajaxUpdateWeiboCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
        updateCookiesUtils(cookiesStr, weiboCookiesMapKey);
    }

    /**
     * 更新torr's cookies
     * @param request
     * @param response
     * @param modelMap
     * @param cookiesStr
     * @return
     */
    @RequestMapping("/updateTorrCookies")
    public String updateTorrCookies(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
            String cookiesStr) {
        updateCookiesUtils(cookiesStr, torrCookiesMapKey);
        return "/cms/cookies/updateTorrCookies";
    }
    
    /**
     * mongodb通用保存cookies值
     * @param cookiesStr
     * @param cookiesMapKey
     */
    public void updateCookiesUtils(String cookiesStr, String cookiesMapKey) {
        JSONObject jsonObj = new JSONObject();
        Query query = new Query(Criteria.where("cookiesName").is(cookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, String> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (StringUtils.isNotBlank(cookiesStr)) {
            jsonObj.put("cookies", cookiesStr);
            Map<String, String> cookies = new HashMap<String, String>();
            String[] cooks = cookiesStr.split(";");
            for (String cs : cooks) {
                if (cs.indexOf("=") != -1) {
                    String key = cs.split("=")[0].trim();
                    String val = cs.split("=")[1].trim();
                    cookies.put(key, val);
                }
            }
        }
        
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            if (!jsonObj.toMap().isEmpty()) {
                Update update = new Update();
                update.set("cookiesJson", jsonObj.toString());
                update.set("updateTime", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, cookiesTable);
            } else {
                String cookiesJson = findCookiesMap.get("cookiesJson");
                jsonObj = new JSONObject(cookiesJson);
                cookiesStr = jsonObj.get("cookies") + "";
            }
        } else {
            Map<String, String> saveMap = new HashMap<String, String>();
            saveMap.put("cookiesName", cookiesMapKey);
            saveMap.put("cookiesJson", jsonObj.toString());
            saveMap.put("updateTime", System.currentTimeMillis() + "");
            mongoTemplate.save(saveMap, cookiesTable);
        }
    }

    public Map<String, String> getWeiboCookies() {
        return getCookies(weiboCookiesMapKey);
    }
    public String getWeiboCookiesString() {
        return getCookiesString(weiboCookiesMapKey);
    }
    
    public Map<String, String> getDoubanCookies() {
        return getCookies(doubanCookiesMapKey);
    }
    
    public Map<String, Object> getDoubanCookiesOthers() {
        return getDoubanCookiesOthers(doubanCookiesMapKey);
    }

    private String getCookiesString(String cookiesMapKey) {
        Query query = new Query(Criteria.where("cookiesName").is(cookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, String> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            String cookiesJson = findCookiesMap.get("cookiesJson");
            JSONObject jsonObj = new JSONObject(cookiesJson);
            String cookiesStr = jsonObj.get("cookies") + "";
            return cookiesStr;
        }
        return null;
    }
    private Map<String, Object> getCookiesStringMap(String cookiesMapKey) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Query query = new Query(Criteria.where("cookiesName").is(cookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, Object> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            String cookiesJson = findCookiesMap.get("cookiesJson").toString();
            JSONObject jsonObj = new JSONObject(cookiesJson);
            String cookiesStr = jsonObj.get("cookies") + "";
            String updateTime = findCookiesMap.containsKey("updateTime") ? findCookiesMap.get("updateTime").toString() : null;
            if (StringUtils.isNotEmpty(updateTime))
                resultMap.put("updateTime", DateUtils.convertDateTimeToString(new Date(Long.parseLong(updateTime))));
            resultMap.put("cookiesStr", cookiesStr);
            return resultMap;
        }
        return null;
    }
    private Map<String, String> getCookies(String cookiesMapKey) {
        Map<String, String> cookies = new HashMap<String, String>();
        Query query = new Query(Criteria.where("cookiesName").is(cookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, String> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            String cookiesJson = findCookiesMap.get("cookiesJson");
            JSONObject jsonObj = new JSONObject(cookiesJson);
            String cookiesStr = jsonObj.get("cookies") + "";
            String[] cooks = cookiesStr.split(";");
            for (String cs : cooks) {
                if (cs.indexOf("=") != -1) {
                    String key = cs.split("=")[0].trim();
                    String val = cs.split("=")[1].trim();
                    cookies.put(key, val);
                }
            }
        }
        return cookies;
    }
    
    private Map<String, Object> getDoubanCookiesOthers(String cookiesMapKey) {
        Map<String, Object> doubanCookiesResult = new HashMap<String, Object>();
        Map<String, String> cookies = new HashMap<String, String>();
        Query query = new Query(Criteria.where("cookiesName").is(cookiesMapKey));
        @SuppressWarnings("unchecked")
        Map<String, String> findCookiesMap = mongoTemplate.findOne(query, Map.class, cookiesTable);
        if (findCookiesMap != null && !findCookiesMap.isEmpty()) {
            String cookiesJson = findCookiesMap.get("cookiesJson");
            JSONObject jsonObj = new JSONObject(cookiesJson);
            String cookiesStr = jsonObj.get("cookies") + "";
            String addItemUrl = jsonObj.get("addItemUrl") + "";
            String ck = jsonObj.get("ck") + "";
            doubanCookiesResult.put("addItemUrl", addItemUrl);
            doubanCookiesResult.put("ck", ck);
            String[] cooks = cookiesStr.split(";");
            for (String cs : cooks) {
                if (cs.indexOf("=") != -1) {
                    String key = cs.split("=")[0].trim();
                    String val = cs.split("=")[1].trim();
                    cookies.put(key, val);
                }
            }
        }
        doubanCookiesResult.put("cookiesMap", cookies);
        return doubanCookiesResult;
    }
}
