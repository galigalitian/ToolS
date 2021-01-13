package com.tools.es.dao;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.tools.es.vo.ChapterContentPartEsVo;

public interface ChapterContentPartRepository extends ElasticsearchRepository<ChapterContentPartEsVo, String> {
    
    List<ChapterContentPartEsVo> findByContent(String content);
}
