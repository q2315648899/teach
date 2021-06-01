package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.controller.MediaUploadController;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Create by wong on 2021/6/1
 */
@Service
public class MediaUploadService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadController.class);

    @Autowired
    MediaFileRepository mediaFileRepository;

    //上传文件根目录
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;


    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        // 1,检查文件在磁盘上是否存在
        // 得到文件所属目录的路径
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        // 得到文件路径
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);

        //2,检查文件在MongoDB中是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //文件存在直接返回
        if (file.exists() && optional.isPresent()) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }

        // 文件不存在时做一些准备工作，检查文件所在目录是否存在，如果不存在就创建
        boolean fileFold = createFileFold(fileMd5);
        if (!fileFold) {
            //上传文件目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 得到文件所属目录的路径
    private String getFileFolderPath(String fileMd5) {
        String fileFolderPath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1,
                2) + "/" + fileMd5 + "/";
        return fileFolderPath;
    }

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     *
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5, String fileExt) {
        String filePath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) +
                "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    // 创建文件目录
    private boolean createFileFold(String fileMd5) {
        //创建上传文件目录
        String fileFolderPath = getFileFolderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            //创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }


    /**
     * 分块检查
     *
     * @param fileMd5   文件唯一标识
     * @param chunk     当前分块下标
     * @param chunkSize 当前分块大小
     * @return
     */
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        // 检查分块文件是否存在
        // 得到分块文件所在目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        // 块文件的文件名称以1,2,3..序号命名，没有扩展名
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        } else {
            return new CheckChunkResult(CommonCode.SUCCESS, false);
        }
    }

    // 得到分块文件所在目录
    private String getChunkFileFolderPath(String fileMd5) {
        String fileChunkFolderPath = getFileFolderPath(fileMd5) + "/" + "chunks" + "/";
        return fileChunkFolderPath;
    }


    // 上传分块
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, Integer chunk) {
        if(file == null){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_ISNULL);
        }
        // 检查分块目录，如果不存在则要创建
        // 创建分块目录
        boolean fileFold = this.createChunkFileFolder(fileMd5);
        if (!fileFold) {
            // 分块文件目录创建失败
            ExceptionCast.cast(MediaCode.CHUNK_FILE_CREATEFOLDER_FAIL);
        }
        // 得到块文件对象
        File chunkFile = new File(getChunkFileFolderPath(fileMd5) + chunk);
        //上传的块文件
        InputStream inputStream= null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("upload chunk file fail:{}",e.getMessage());
            ExceptionCast.cast(MediaCode.CHUNK_FILE_UPLOAD_FAIL);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //创建块文件目录
    private boolean createChunkFileFolder(String fileMd5) {
        //创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            //创建文件夹
            boolean mkdirs = chunkFileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }
}
