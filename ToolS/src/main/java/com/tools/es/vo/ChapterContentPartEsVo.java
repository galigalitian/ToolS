package com.tools.es.vo;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "tools", indexStoreType = "chaptercontentpart")
public class ChapterContentPartEsVo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 9108327728415407918L;
    @Id
    private String id;
    @Field()
    private String content;
    private Long createTime;
    private String type;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
}
