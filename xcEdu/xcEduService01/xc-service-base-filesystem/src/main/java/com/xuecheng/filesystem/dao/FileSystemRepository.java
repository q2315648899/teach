package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Create by wong on 2021/5/27
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
