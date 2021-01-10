package com.tools.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Configurable
public class ElasticSearchConfiguration {
    
    @Value("${elasticsearch.ip}")
    String[] ipAddress;
    
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
                )
        );
        return restHighLevelClient;
    }

}
