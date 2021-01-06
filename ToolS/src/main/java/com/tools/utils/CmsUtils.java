package com.tools.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.movie.cms.download.CmsDownloadUtilsController;
import com.movie.common.UploadPathUtil;

public class CmsUtils {
    
    static Logger logger = LoggerFactory.getLogger(CmsUtils.class);
    
    public static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
    public static Map<String, Object> transBeanToMap(Object obj) {
        Map<String, Object> beanMap = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (!beanMap.containsKey(key))
                        beanMap.put(key, value);
                }
            }
        } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return beanMap;
    }
    
    public static String convertPath(String path, boolean hasAbsolutePath) {
        if (StringUtils.isNotBlank(path)) {
            if (File.separator.equals("/")) { //linux系统
                path = path.replaceAll("\\\\", "\\/");
            } else { //window系统
                path = path.replaceAll("\\/", "\\\\");
            }
            if (hasAbsolutePath) {
                String absolutePath = UploadPathUtil.BASE_PATH;
                if (!File.separator.equals("/")) { //windows系统
                    absolutePath = absolutePath + "src\\main\\webapp\\";
                }
                path = absolutePath + path;
            }
            return path;
        }
        return "";
    }
    
    public static String converFilePathToUrlPath(String path) {
        if (StringUtils.isNotBlank(path)) {
            path = path.replaceAll("\\\\", "\\/");
            return path;
        }
        return "";
    }
    
    /**
     * copy文件
     * @param source
     * @param target
     * @param hasAbsolutePath
     */
    public static void copyFileByIO(String source, String target, boolean hasAbsolutePath) {
        if (hasAbsolutePath) {
            copyFileByIO(convertPath(source, true), convertPath(target, true));
        } else copyFileByIO(source, target);
    }
    
    public static void copyFileByIO(String source, String target) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        try {
            FileUtils.copyFile(sourceFile, targetFile);
        } catch (IOException e) {
            logger.error("复制文件错误：" + e);
        }
    }
    
    /**
     * 解析中字文件
     * @param file
     * @return map <br>
     *  treeList: 种子文件树结构 <br>
     *  downloadNameList: 所有电影名字列表 <br>
     *  downloadSizeList: 所有电影大小列表 <br>
     *  magnet_url: 磁力 <br>
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static Map<String, Object> torrentFile(File file, String[] blackNames) throws IllegalArgumentException, IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        TorrentFile torrentFile = new TorrentFile(file);
        String magnet_url = "magnet:?xt=urn:btih:" + torrentFile.getHexHash();
        String[] downloadFileNames = torrentFile.getFilenames();
        long [] downloadFileLengths = torrentFile.getLengths();
        List<String> treeNameList = new ArrayList<String>();
        List<String> downloadNameList = new ArrayList<String>();
        List<String> downloadSizeList = new ArrayList<String>();
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < downloadFileNames.length; i++) {
            String downloadFileName = downloadFileNames[i];
            String downloadName = new String(downloadFileName.getBytes("iso8859-1"), "utf-8");
            String mNames[] = downloadName.split(File.separator.equals("/") ? "/" : "\\\\");
            for (int n = 0; n < mNames.length; n++) {
                String mName = mNames[n];
                int pId = n;
                Map<String, Object> treeMap = new HashMap<String, Object>();
                if (pId != 0) treeMap.put("id", pId + 10 + i);
                else treeMap.put("id", pId + i + 1);
                treeMap.put("pId", pId);
                treeMap.put("name", mName);
                if (!treeNameList.contains(mName)) {
                    treeNameList.add(mName);
                    list.add(treeMap);
                }
                if (blackNames != null && blackNames.length > 0) {
                    for (String blackName : blackNames) {
                        if (mName.toLowerCase().trim().indexOf(blackName) != -1) {
                            return null;
                        }
                    }
                }
                if (CmsDownloadUtilsController.isMedia(mName)) {
                    String unit = "m";
                    double totalLength = downloadFileLengths[i] / (double) 1024l;
                    if (totalLength > 1024) {
                        totalLength = totalLength / (double) 1024l;
                        unit = "m";
                    }
                    if (totalLength > 1024) {
                        totalLength = totalLength / (double) 1024l;
                        unit = "g";
                    }
                    BigDecimal totalBd = new BigDecimal(totalLength);
                    Double size = totalBd.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    downloadSizeList.add(size + unit);
                    downloadNameList.add(mName);
                }
            }
        }
        
        map.put("treeList", list);
        map.put("downloadNameList", downloadNameList);
        map.put("downloadSizeList", downloadSizeList);
        map.put("magnet_url", magnet_url);
        return map;
    }
    
}
