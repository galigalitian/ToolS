package com.tools.cms.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tools.cms.cookies.CmsCookiesUtilsController;
import com.tools.utils.UploadPathUtil;

@RequestMapping("/cms/upload")
@Controller
public class CmsUploadController {
    @Autowired
    private CmsCookiesUtilsController cmsCookiesUtilsController;
    
    @ResponseBody
    @RequestMapping({"/uploadWeiboCookiesFile", "/uploadWeiboCookiesFile/"})
    public String uploadWeiboCookiesFile(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("uploadWeiboCookiesFile");
        String filename = multipartFile.getOriginalFilename();
        String filetype = filename.substring(filename.lastIndexOf(".")).trim().toLowerCase();
        if (filename.indexOf("cookie") != -1 && filetype.equals(".txt")) {
            return uploadWeiboCookies(multipartFile, filetype);
        }
        return null;
    }
    
    
    private static final String weiboCookiesMapKey = "weiboCookies";
    private String uploadWeiboCookies(MultipartFile multipartFile, String filetype) {
        String realPath = UploadPathUtil.BASE_PATH + 
                (File.separator.equals("/") ? "upload" : "src/main/webapp/upload") + File.separator; //F:\\workspace\\movie\\upload\\tmp_movie_subtitle\\
        File dirFile = new File(realPath);
        if (!dirFile.exists()) dirFile.mkdirs();
        String filename = realPath + "weiboCookies.txt";
        File file = new File(filename);
        if (file.exists()) file.delete();
        BufferedReader bufferedReader = null;
        try {
            multipartFile.transferTo(file);
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            StringBuffer strBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] lineArr = line.split("\\s+");
                String cm = "";
                for (int i = lineArr.length - 2; i < lineArr.length; i++) {
                    if (cm.equals("")) cm = lineArr[i];
                    else cm = cm + "=" + lineArr[i] + ";";
                }
                if (!cm.equals("")) strBuffer.append(cm);
            }
            cmsCookiesUtilsController.updateCookiesUtils(strBuffer.toString(), weiboCookiesMapKey);
            return strBuffer.toString();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return "";
    }
}
