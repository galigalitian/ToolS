package com.tools.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 其他关联电影列表
 * @author TIANWEI
 *
 */
@Controller
@RequestMapping("/cms")
public class CmsController {
    
    @RequestMapping({"/index", "/", ""})
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        return "/cms/index";
    }
    
    @RequestMapping("/index_v2")
    public String indexV2(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        return "/cms/index/index_v2";
    }
    
}
