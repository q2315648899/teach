package com.xuecheng.manage_cms;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Create by wong on 2021/5/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {

    // SpringMVC提供 RestTemplate请求http接口，RestTemplate的底层可以使用第三方的http客户端工具实现http 的请求
    @Autowired
    GridFsTemplate gridFsTemplate;

    // 存文件
    // 存储原理说明：
    // 文件存储成功得到一个文件id
    // 此文件ID是fs.files集合中的主键。
    // 可以通过文件id查询fs.chunks表中的记录，得到文件的内容。
    @Test
    public void testGridFsTemplate() throws FileNotFoundException {
        //要存储的文件
        File file = new File("d:/index_banner.ftl");
        //定义输入流
        FileInputStream inputStram = new FileInputStream(file);
        //向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(inputStram, "index_banner.ftl", "");
        //得到文件ID
        String fileId = objectId.toString();
        System.out.println(file);
    }

}
