package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Create by wong on 2021/6/1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFile {

    // 测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        // 源文件
        File sourceFile = new File("D:\\ffmpeg_test\\lucene.avi");
        // 块文件目录
        String chunkFileFolder = "D:\\ffmpeg_test\\chrunks\\";

        // 先定义块文件大小
        long chunkFileSize = 1024 * 1024 * 1;

        // 块数量
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);

        // 创建读文件的对象，使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        //缓冲区大小
        byte[] b = new byte[1024];
        //分块
        for (int i = 0; i < chunkFileNum; i++) {
            //创建分块文件
            File chrunkFile = new File(chunkFileFolder + i);
            boolean newFile = chrunkFile.createNewFile();
            if (newFile) {
                //创建向块文件写的对象，向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(chrunkFile, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    // 如果块文件的大小达到1M，开始写下一个块文件
                    if (chrunkFile.length() > chunkFileSize) {
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    // 测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        // 块文件目录
        String chunkFileFolderPath = "D:\\ffmpeg_test\\chrunks\\";
        // 块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        // 块文件列表
        File[] files = chunkFileFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(files));
        // 按块文件名称从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });

        // 定义合并文件对象
        File mergeFile = new File("D:\\ffmpeg_test\\lucene_merge.avi");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建新的合并文件
        boolean newFile = mergeFile.createNewFile();

        //创建写文件的对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //缓冲区
        byte[] b = new byte[1024];

        //合并文件
        for (File chunkFile : fileList) {
            // 创建读块文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
