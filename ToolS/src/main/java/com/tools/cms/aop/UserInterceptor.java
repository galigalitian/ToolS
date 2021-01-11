package com.tools.cms.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.tools.utils.CookieUtil;
import com.tools.utils.SessionManager;

/**
 * @author TIANWEI
 * @version 创建时间 2014-6-14 下午04:52:02
 * $Revision$ $Date$
 */
@Aspect
@Component
public class UserInterceptor {
    @Autowired
    private SessionManager sessionManager;
    
    @Pointcut("execution(* com.tools.weibo.controller.WeiboUtilsController.*(..)) ||" +
            "execution(* com.tools.cms.CmsController.*(..))")
    public void loginSession() {}
    
    /**
     * 首页
     * @param pjp
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Throwable
     */
    @Around("loginSession()&&args(request,response,modelMap,..)")
    public Object loginSession(ProceedingJoinPoint pjp, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Throwable {
        if (sessionManager != null && sessionManager.getUser_name() != null &&
                sessionManager.getPassword() != null) {
            String user_name = sessionManager.getUser_name();
            modelMap.addAttribute("user_name", user_name);
        } else {
            String jtun = CookieUtil.readCookie(request, "jtun");
            String jtpsd = CookieUtil.readCookie(request, "jtpsd");
            if (jtun != null && jtun.equals("tianwei") &&
                    jtpsd != null && jtpsd.equals("qiuli!(*%0903")) {
                sessionManager.setUser_name(null);
                sessionManager.setPassword(null);
                sessionManager.setUser_name(jtun);
                sessionManager.setPassword(jtpsd);
                return pjp.proceed();
            } else {
                response.sendRedirect("/cms/login");
            }
            return null;
        }
        return pjp.proceed();
    }
    
//    private void ssoLogin(HttpServletRequest request, HttpServletResponse response) {
//        String userid = CookieUtil.readCookie(request, "nkid");
//        String password = CookieUtil.readCookie(request, "nkpsd");
//        String logintype = CookieUtil.readCookie(request, "nktp");
//        UserCms sessionUserInfo = sessionManager.getUserinfo();
//        if (userid == null || userid.equals("") || password == null || password.equals("")) {
//            sessionUserInfo = null;
//            sessionManager.setUserinfo(sessionUserInfo);
//        } else {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("user_id", userid);
//            if (sessionUserInfo == null) {
//                sessionManager.setUserinfo(null);
//                sessionUserInfo = new UserCms();
//                sessionUserInfo.setUser_id(Integer.parseInt(userid));
//                sessionUserInfo = userService.getUser(map);
//                if (sessionUserInfo != null) {
//                    String md5pass = CookieUtil.getMd5(sessionUserInfo.getPassword() == null ? "" : sessionUserInfo.getPassword());
//                    if (sessionUserInfo.getPassword() != null
//                        && !sessionUserInfo.getPassword().equals("")
//                        && password.equals(md5pass)) {
//                        if (logintype != null && !logintype.equals("")) sessionUserInfo.setLogin_type(logintype);
//                        sessionManager.setUserinfo(sessionUserInfo);
//                    }
//                } else {
//                    sessionManager.setUserinfo(null);
//                    CookieUtil.logoutCookie(response);
//                }
//            } else {
//                //sessionUserInfo = userService.getUser(map);
//                if (sessionManager.getUserinfo() == null)
//                    sessionManager.setUserinfo(sessionUserInfo);
//            }
//            
//        }
//    }
}
