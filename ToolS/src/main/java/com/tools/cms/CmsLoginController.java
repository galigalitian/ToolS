package com.tools.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tools.utils.CookieUtil;
import com.tools.utils.SessionManager;

/**
 * @author TIANWEI
 * @version 创建时间 2014-6-14 下午06:06:49
 * $Revision$ $Date$
 */
@RequestMapping("/cms")
@Controller
public class CmsLoginController {
    
    @Autowired
    private SessionManager sessionManager;
    
    private Logger logger = LoggerFactory.getLogger(CmsLoginController.class);
    
    /**
     * 登录
     * @param request
     * @param response
     * @param modelMap
     * @param user_name
     * @param password
     * @return
     */
    @RequestMapping(value = {"/login", "/login/"})
    public String login(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String user_name = request.getParameter("user_name");
        String password = request.getParameter("password");
        String version = request.getParameter("version");
        modelMap.addAttribute("version", version);
        if (user_name != null && !user_name.equals("") && password != null && !password.equals("")) {
            if (user_name.equals("tools_admin") && password.equals("99piyanping")) {
                sessionManager.setUser_name(null);
                sessionManager.setPassword(null);
                sessionManager.setUser_name(user_name);
                sessionManager.setPassword(password);
                CookieUtil.writeCmsCookie(response, 60*60*24*3, user_name, password);
                logger.info("用户：xxxx 登录后台系统成功");
                if (version.equals("v2"))
                    return "redirect:/cms/index_v2";
                else
                    return "redirect:/cms/";
            } else {
                //modelMap.addAttribute("errorCode", Code.LOGIN_ERROR);
                return "/cms/login";
            }
        } else return "/cms/login";
    }
    
    /**
     * 退出登录
     * @param request
     * @param response
     * @param modelMap
     * @return
     */
    @RequestMapping(value = {"/logout", "/logout/"})
    public String logout(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        CookieUtil.logoutCookie(response);
        sessionManager.setUser_name(null);
        sessionManager.setPassword(null);
        return "redirect:/";
    }
    
//    @RequestMapping(value = {"/ajaxLogin", "/ajaxLogin/"})
//    public void ajaxLogin(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, String user_name, String password) {
//        if (user_name != null && !user_name.equals("") && password != null && !password.equals("")) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("user_name", user_name);
//            map.put("password", password);
//            UserCms user = userService.getUser(map);
//            if (user != null) {
//                CookieUtil.writeCookie(response, user.getUser_id().toString(), user.getPassword(), "web", 60*60*24*365*2);
//                sessionManager.setUserinfo(null);
//                sessionManager.setUserinfo(user);
//                user.setIs_login_time(1);
//                userService.updateUser(user);
//                WebUtil.outMessage(Code.SUCCESS, response);
//            } else {
//                WebUtil.outMessage(Code.LOGIN_ERROR, response);
//            }
//        } else WebUtil.outMessage(Code.LOGIN_ERROR, response);
//    }
}
