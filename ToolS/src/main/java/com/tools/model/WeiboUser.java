package com.tools.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class WeiboUser {
    @Id
    private String id;
    private String userStr;
    @Indexed
    private String userUrl;
    @Indexed
    private int isSpider;
    @Indexed
    private Date create_time;
    @Indexed
    private Date update_time;
    @Indexed
    private int status;
    
    private long commentCount;
    @Indexed
    private Long lastVisitWeiboTime;
    
    public long getCommentCount() {
        return commentCount;
    }
    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }
    public String getUserUrl() {
        return userUrl;
    }
    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserStr() {
        return userStr;
    }
    public void setUserStr(String userStr) {
        this.userStr = userStr;
    }
    public Date getCreate_time() {
        return create_time;
    }
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getIsSpider() {
        return isSpider;
    }
    public void setIsSpider(int isSpider) {
        this.isSpider = isSpider;
    }
    public Date getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
    public Long getLastVisitWeiboTime() {
        return lastVisitWeiboTime;
    }
    public void setLastVisitWeiboTime(Long lastVisitWeiboTime) {
        this.lastVisitWeiboTime = lastVisitWeiboTime;
    }
    
}
