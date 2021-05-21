package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MongoDB的配置类
 * Create by wong on 2021/5/21
 */
// @Configuration属于spring，相当于注入一个Bean,spring容器启动时会扫描到这个Bean注入到IOC容器中
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String db;

    // GridFSBucket用于打开下载流对象
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient){
        MongoDatabase database = mongoClient.getDatabase(db);
        GridFSBucket bucket = GridFSBuckets.create(database);
        return bucket;
    }

}
