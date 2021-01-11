package com.tools.story.controller;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.tools.es.dao.ChapterContentPartRepository;
import com.tools.es.vo.ChapterContentPartEsVo;

@RestController
@RequestMapping("/story")
public class ChapterContentController {
    private static String regex = "：|:|“|”|\\\"|。|\\.|？|\\?|、|，|,|！|!|@|#|￥|%|\\&|\\$|……|\\*|\\^|（|）|\\(|\\)|-|\\+|=|\\~|`|·|[|]|【|】|《|》|<|\\>|\\/|\\\\|｛|｝|\\{|\\}|\\|";
    @Autowired
    private ChapterContentPartRepository chapterContentPartRepository;
    @Autowired
    private RestHighLevelClient client;
    
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String index() {
        return "chapterContent";
    }
    
    @RequestMapping(path = "/addContent", method = RequestMethod.GET)
    public void addContent() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("d:\\chapterContent.txt")));
            String line = null;
            int n = 0;
            while ((line = bufferedReader.readLine()) != null) {
                n++;
                String lineArr[] = line.split(regex);
                List<ChapterContentPartEsVo> contentVoList = Lists.newArrayList();
                for (String str : lineArr) {
                    if (StringUtils.isNotBlank(str)) {
                        str = str.replaceAll("\\s*", "");
                        if (str.length() > 1) {
                            search(str, false);
                            ChapterContentPartEsVo chapterContentPartEsVo = new ChapterContentPartEsVo();
                            chapterContentPartEsVo.setContent(str.replaceAll("\\s*", ""));
                            chapterContentPartEsVo.setCreateTime(System.currentTimeMillis());
                            contentVoList.add(chapterContentPartEsVo);
                        }
                    }
                }
                chapterContentPartRepository.saveAll(contentVoList);
                if (n > 100) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //chapterContentPartRepository.save(entity);
    }
    
    private List<Map<String, Object>> search(String q, boolean isKeyword) {
        List<Map<String, Object>> resultMap = Lists.newArrayList();
        SearchRequest searchRequest = new SearchRequest("tools");
        //searchRequest.searchType("_doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        try {
            String keyword = "";
            if (isKeyword) keyword = ".keyword";
            sourceBuilder.query(QueryBuilders.termQuery("content" + keyword, URLDecoder.decode(q, "utf-8"))).from(0).size(1000);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println(hits.getTotalHits());
            for (SearchHit hit : hits.getHits()) {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                resultMap.add(sourceMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    
    @GetMapping("/search")
    public List<Book> testSearch(@RequestParam String q) {
        search(q, false);
        return null;
    }
    
    public static void main(String args[]) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("d:\\chapterContent.txt")));
            String line = null;
            StringBuffer strBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                String lineArr[] = line.split(regex);
                List<ChapterContentPartEsVo> contentVoList = Lists.newArrayList();
                for (String str : lineArr) {
                    if (StringUtils.isNotBlank(str) && str.length() > 1) {
                        ChapterContentPartEsVo chapterContentPartEsVo = new ChapterContentPartEsVo();
                        chapterContentPartEsVo.setContent(str);
                        chapterContentPartEsVo.setCreateTime(System.currentTimeMillis());
                        contentVoList.add(chapterContentPartEsVo);
                    }
                }
                //strBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*String aa = "听到我的这番话，刘娣的心情这才|算有些好转，她指着|我的脑袋，道：“如果有什么{问题}，我拿你是问！”";
        String aas[] = aa.split("：|:|“|”|\\\"|。|\\.|？|\\?|，|,|！|!|@|#|￥|%|\\&|\\$|……|\\*|\\^|（|）|\\(|\\)|-|\\+|=|\\~|`|·|[|]|【|】|《|》|<|\\>|\\/|\\\\|｛|｝|\\{|\\}|\\|");
        for (String a : aas) {
            System.out.println(a);
        }*/
    }
}
