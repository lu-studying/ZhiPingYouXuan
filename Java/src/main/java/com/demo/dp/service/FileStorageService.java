package com.demo.dp.service;

import java.io.InputStream;

/**
 * 本地文件存储服务接口
 * 
 * 功能说明：
 * 1. 上传文件到本地存储
 * 2. 返回文件的访问 URL
 * 3. 删除本地文件
 */
public interface FileStorageService {

    /**
     * 上传文件到本地存储
     * 
     * @param inputStream 文件输入流
     * @param originalFilename 原始文件名（用于获取文件扩展名）
     * @param pathPrefix 存储路径前缀（如 "menu/"）
     * @return 文件的访问 URL（如 "/uploads/menu/2024/01/15/xxx.jpg"）
     * @throws Exception 上传失败时抛出异常
     */
    String uploadFile(InputStream inputStream, String originalFilename, String pathPrefix) throws Exception;

    /**
     * 删除本地文件
     * 
     * @param fileUrl 文件的访问 URL 或相对路径
     * @throws Exception 删除失败时抛出异常
     */
    void deleteFile(String fileUrl) throws Exception;
}


