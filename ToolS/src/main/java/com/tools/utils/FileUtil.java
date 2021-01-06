package com.tools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.movie.cms.utils.CmsUtils;

/**
 * @author TIAN WEI
 * @version 创建时间：Aug 5, 2009 8:24:40 PM
 * $Revision$ $Date$
 *
 */
public class FileUtil {
    public static ArrayList<String> getAllImagePaths(String path) throws IOException {
        ArrayList<String> imagePaths = new ArrayList<String>();
        File[] files = new File(path).listFiles();
        for(File file : files) {
            if (file != null && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png"))) {
                imagePaths.add(file.getCanonicalPath());
            }
            if (file.isDirectory()) {
                ArrayList<String> tempImages = getAllImagePaths(file.getCanonicalPath());
                if (tempImages != null) {
                    imagePaths.addAll(tempImages);
                }
            }
        }
        if (imagePaths.size() > 0)
            return imagePaths;
        else 
            return null;
    }
    
    @SuppressWarnings("resource")
    private static String abstractReadFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuffer strBuffer = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            strBuffer.append(line);
        }
        return strBuffer.toString();
    }
    
    public static String readFile(File file) throws IOException {
        return abstractReadFile(file);
    }
    
    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        return readFile(file);
    }
    
    @SuppressWarnings("resource")
    public static void writeFile(String filePath, String text) throws IOException {
        FileWriter fileWrite = new FileWriter(filePath, true);
        fileWrite.write(text);
        fileWrite.close();
    }
    
    @SuppressWarnings("resource")
    public static void writeFile(File file, String[] texts) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        for (String text : texts)
            fileWriter.write(text + "\r\n");
        fileWriter.flush();
        fileWriter.close();
    }
    
    /**
     * 相对路径
     * @param uploadPath
     * @return eg: upload/aaaaa/
     * @throws IOException
     */
    public static String getPhysicalPath(String uploadPath) {
        try {
            String absolutePath = getAbsolutePath();
            uploadPath = uploadPath != null && !uploadPath.equals("") ?
                    (uploadPath + System.getProperty("file.separator")) : "";
            String realPath = "upload" 
                              + System.getProperty("file.separator") 
                              + uploadPath;
            File realFile = new File(absolutePath + realPath);
            if (!realFile.isDirectory()) {
                realFile.mkdirs();
            }
            realPath = realPath.replace("\\", "/");
            return realPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 得到绝对路径
     * @return eg:F:\workspace\onek_trunk\web\
     */
    public static String getAbsolutePath() {
        URL url = FileUtil.class.getResource("/");
        File file = new File(url.getPath()).getParentFile().getParentFile();
        String absolutePath = file.getAbsolutePath() + System.getProperty("file.separator");
        return absolutePath;
    }
    
    
    public static String createDir(String filepath) {
    	String abspath = getAbsolutePath();
    	String path = abspath + filepath + System.getProperty("file.separator");
    	File file = new File(path);
    	boolean b = false;
    	if (!file.exists()) {
    		b = file.mkdirs();
    	} else {
    	    b = true;
    	}
    	return b ? path : null;
    }
    
    public static String createDirNoPath(String filepath) {
        File file = new File(filepath);
        boolean b = false;
        if (!file.exists()) {
            b = file.mkdirs();
        } else {
            b = true;
        }
        return b ? filepath : null;
    }
    public String getFilePath(int filetype, String filename, String uploadPath) {
        return null;
    }
    
    public static String getFilePath(String path) {
        if (path.toLowerCase().startsWith("http://")) {
            return path;
        } else {
            return path.startsWith("/") ? getAbsolutePath() + path.substring(path.indexOf("/")) :
                getAbsolutePath() + path;
        }
    }
    
    public static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        boolean flag = false;
        if (file.exists()) {
            flag = file.delete();
        }
        return flag;
    }
    
    public static boolean deleteFile(File file) {
        boolean flag = false;
        if (file.exists()) {
            flag = file.delete();
        }
        return flag;
    }
    
    public static File lastFileName(String path, String lastname) {
        File file = new File(path);
        
        File[] files = file.listFiles(new DirectoryFilter());
        if (files.length == 0) {
            return file;
        }
        File lastfile = files[0];
        for (File lfile : files) {
            if (lfile.lastModified() >= lastfile.lastModified()) lastfile = lfile;
        }
        return lastFileName(path + "/" + lastfile.getName(), lastfile.getName());
    }
    
    //private static int numm = 0;
    //private static char cc = (char)('a'-1);
    /**
     * 图片路径存储方案
     * @param basicpath
     * @param month
     * @return
     * @throws IOException
     */
    public static String getNewFilePath(String basicpath, String month) throws IOException {
        return "";
    }
    
    public static String getFilePhysicalLocation(long userid, String uploadPath) {
        String[] dirs = new String[3];
        for (int i = 1 ; i < 4 ; i ++) {
            long temp = (long)(userid / Math.pow(10,i));
            dirs[i-1] = (new Long(temp % 10)).toString();
        }
        File dir = new File(FileUtil.getAbsolutePath()
                + uploadPath
                + System.getProperty("file.separator")
                + dirs[2] 
                + System.getProperty("file.separator") 
                + dirs[1]
                + System.getProperty("file.separator") 
                + dirs[0]);
        if (!dir.isDirectory()) dir.mkdirs();
        String tempdir = dir.toString();
        int i = tempdir.indexOf("uploadimg");
        tempdir = tempdir.substring(i);
        tempdir = tempdir.replace('\\','/');
        return tempdir;
    }
    
    public static String getFilePhysicalLocation(long userid) {
        String[] dirs = new String[3];
        for (int i = 1 ; i < 4 ; i ++) {
            long temp = (long)(userid / Math.pow(10,i));
            dirs[i-1] = (new Long(temp % 10)).toString();
        }
        String tempdir = dirs[2] 
                              + System.getProperty("file.separator") 
                              + dirs[1]
                              + System.getProperty("file.separator") 
                              + dirs[0];
        return  tempdir.replace('\\','/');
    }
    
    public static void copyFile(String source, String targe) {
        String realPath = targe.substring(0, targe.lastIndexOf("/"));
        File dirFile = new File(realPath);
        if (!dirFile.exists()) dirFile.mkdirs();
        try {
            File targetFile = new File(targe);
            FileInputStream in = new FileInputStream(new File(source));
            FileOutputStream out = new FileOutputStream(targetFile);
            byte[] contents = new byte[in.available()];
            in.read(contents);
            out.write(contents);
            in.close();
            out.flush();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * eg: aa/bb/
     * @param uuid
     * @param basicpath
     * @return
     */
    public static String getFileLocation(String uuid, String basicpath) {
        String path = FileUtil.getAbsolutePath();
        String location = uuid.substring(0, 2) + System.getProperty("file.separator") +
                           uuid.substring(2, 4) + System.getProperty("file.separator");
        File file = new File(path + basicpath + System.getProperty("file.separator") + location);
        if (!file.isDirectory()) file.mkdirs();
        return location.replace('\\', '/');
    }
    
    /**
     * eg: aa/bb/
     * @param uuid
     * @param basicpath
     * @return
     */
    public static String getRemoteFileLocation(String uuid, String basicpath, String remote) {
        String path = remote;
        String location = uuid.substring(0, 2) + System.getProperty("file.separator") +
                           uuid.substring(2, 4) + System.getProperty("file.separator");
        File file = new File(path + basicpath + System.getProperty("file.separator") + location);
        if (!file.isDirectory()) file.mkdirs();
        return location;
    }
    
    public static String downImage(String url, String targetFile) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
        .setSocketTimeout(5000)
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000)
        .build();
        HttpGet httpget = new HttpGet(url);
        
        httpget.setConfig(requestConfig);
        httpget.setHeader("User-Agent", CmsUtils.USER_AGENT);
      
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("http.protocol.version", HttpVersion.HTTP_1_1);
        try {
      
            // Execute the method.
            HttpResponse response = httpClient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Method failed: " + response.getStatusLine());
            }
            
            HttpEntity entity = response.getEntity();
            
            InputStream is = entity.getContent();
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(targetFile);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();  
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetFile;
    }
    
    public static void downloadImageByNio(String source, String target) {
        try {
            URL url = new URL(source);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setConnectTimeout(180000);
            httpconn.setReadTimeout(180000);
            httpconn.setRequestProperty("User-agent", CmsUtils.USER_AGENT);
            InputStream ins = httpconn.getInputStream();
            Path targetPath = Paths.get(target);
            Files.createDirectories(targetPath.getParent());
            Files.copy(ins, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void downloadByNIO(String urlStr, String target) {
        ReadableByteChannel  rbc = null;
        FileOutputStream fos = null;
        FileChannel foutc = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setConnectTimeout(180000);
            httpconn.setReadTimeout(180000);
            httpconn.setRequestProperty("User-agent", CmsUtils.USER_AGENT);
            rbc = Channels.newChannel(httpconn.getInputStream());
            File file = new File(target);
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            foutc = fos.getChannel();
            foutc.transferFrom(rbc, 0, Long.MAX_VALUE);
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rbc != null) {
                try {
                    rbc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (foutc != null) {
                try {
                    foutc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
