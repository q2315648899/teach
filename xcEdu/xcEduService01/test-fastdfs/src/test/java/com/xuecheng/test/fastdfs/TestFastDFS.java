package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    // 上传文件
    @Test
    public void testUpload() {
        try {
            // 加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            // 定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            // 连接TrackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            // 获取storageServer
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            // 创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            // 向storage服务器上传文件
            // 本地文件的路径
            String filePath = "D:/asset-logo.png";
            // 上传成功后拿到文件Id
            String fileId = storageClient1.upload_file1(filePath, "png", null);
            // group1/M00/00/00/wKiJXWCuYzqAHxCeAAARA0xqVgo99..png
            System.out.println(fileId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
