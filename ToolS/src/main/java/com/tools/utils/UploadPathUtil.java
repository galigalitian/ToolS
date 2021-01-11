package com.tools.utils;

import java.io.File;

public class UploadPathUtil {

    /** 基础地址 /mnt/movie/ **/
    public static String BASE_PATH = "";
    public static final String BASE_UPLOAD_PATH = "upload";
    /**data/ **/
    public static final String BASE_DATA_PATH = "data" + File.separator;
    static {
        if (File.separator.equals("/")) { // linux系统
            BASE_PATH = "/mnt/movie/";
        } else { // windows系统
            BASE_PATH = FileUtil.getAbsolutePath();
        }
    }
    
    /** 种子上传地址 upload/torrent/ **/
    public static final String UPLOAD_TORRENT = BASE_UPLOAD_PATH + File.separator + "torrent" + File.separator;
    /** 字幕上传地址 upload/movie_subtitle **/
    public static final String UPLOAD_MOVIE_SUBTITLE = BASE_UPLOAD_PATH + File.separator + "movie_subtitle";
    /** 图片上传地址 upload/movie_cover **/
    public static final String UPLOAD_MOVIE_COVER = BASE_UPLOAD_PATH + File.separator + "movie_cover";
    /** btbtt 电视剧种子保存地址 upload/torrent/btTv/ **/
    public static final String SPIDER_BT_TV_TORRENT = BASE_UPLOAD_PATH + File.separator + "torrent" + File.separator + "btTv" + File.separator;
    /** btbtt 动漫种子保存地址  upload/torrent/btDm/ **/
    public static final String SPIDER_BT_DM_TORRENT = BASE_UPLOAD_PATH + File.separator + "torrent" + File.separator + "btDm" + File.separator;
    /** lucene电影索引地址 movie_index/ **/
    public static final String MOVIE_INDEX = "movie_index" + File.separator;
    /** lucene电影更多的推荐索引地址 movie_more_index/ **/
    public static final String MOVIE_MORE_INDEX = "movie_more_index" + File.separator;
}
