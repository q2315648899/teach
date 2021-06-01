package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Create by wong on 2021/6/2
 */
@Component
public class MediaProcessTask {

    //ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;
    //上传文件根目录
    @Value("${xc-service-manage-media.upload-location}")
    String serverPath;

    @Autowired
    MediaFileRepository mediaFileRepository;

    // 接受视频处理消息进行视频处理
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-processtask}")
    public void receiveMediaProcessTask(String msg) throws IOException {
        // 1、解析消息内容，得到mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        // 2、拿mediaId从数据库查询文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            return;
        }
        MediaFile mediaFile = optional.get();
        String fileType = mediaFile.getFileType();
        if (!"avi".equals(fileType)) {// 目前只处理avi文件
            mediaFile.setProcessStatus("303004");// 无需处理
            return;
        } else {
            // 需要处理
            mediaFile.setProcessStatus("303001");// 处理中
            mediaFileRepository.save(mediaFile);
        }
        // 3、使用工具栏将avi转成mp4
        // String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        // 要处理的视频文件的路径
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        // 生成的mp4的文件名称
        String mp4_name = mediaFile.getFileId() + ".mp4";
        // 生成的mp4所在的路径
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        // 创建工具类对象
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        // 开始编码转成mp4,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        if (result == null || !result.equals("success")) {
            //操作失败写入处理日志
            mediaFile.setProcessStatus("303003");//处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        // 4、把mp4生成m3u8和ts文件
    }
}
