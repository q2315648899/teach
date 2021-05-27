package com.xuecheng.filesystem.controller;

import com.xuecheng.api.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Create by wong on 2021/5/27
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSystemService fileSystemService;

    // 上传文件
    // 添加@RequestParam("file")，@RequestParam(value = "filetag",则请求过来的参数名称必须为“file”，若不加，则请求的参数名称为controller接口中的形参名称
    // @RequestParam(value = "filetag"),@RequestParam(value = "businesskey"),@RequestParam(value = "metadata"）
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        return fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
    }
}
