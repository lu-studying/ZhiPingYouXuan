package com.demo.dp.service.impl;

import com.demo.dp.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储服务实现
 * 
 * 功能说明：
 * 1. 上传文件到本地存储，自动生成唯一文件名
 * 2. 返回文件的访问 URL（相对于服务器根路径）
 * 3. 支持删除文件
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Value("${file.upload.base-path}")
    private String basePath;

    @Value("${file.upload.access-path}")
    private String accessPath;

    @Override
    public String uploadFile(InputStream inputStream, String originalFilename, String pathPrefix) throws Exception {
        try {
            // 生成唯一文件名：使用日期目录 + UUID + 原始文件扩展名
            String fileExtension = getFileExtension(originalFilename);
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;
            
            // 构建完整的存储路径
            // 格式：{basePath}{pathPrefix}{dateDir}/{fileName}
            // 例如：uploads/menu/2024/01/15/abc123.jpg
            String relativePath = buildRelativePath(pathPrefix, dateDir, fileName);
            Path fullPath = Paths.get(basePath, relativePath).toAbsolutePath();
            
            // 确保目录存在
            Files.createDirectories(fullPath.getParent());
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(fullPath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            // 构建并返回访问 URL
            // 格式：{accessPath}{relativePath}
            // 例如：/uploads/menu/2024/01/15/abc123.jpg
            String fileUrl = accessPath + relativePath;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("文件上传失败: {}", originalFilename, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭输入流失败", e);
                }
            }
        }
    }

    @Override
    public void deleteFile(String fileUrl) throws Exception {
        try {
            // 从访问 URL 中提取相对路径
            // 例如：/uploads/menu/2024/01/15/abc123.jpg -> menu/2024/01/15/abc123.jpg
            String relativePath = extractRelativePath(fileUrl);
            
            if (relativePath != null && !relativePath.isEmpty()) {
                Path fullPath = Paths.get(basePath, relativePath).toAbsolutePath();
                File file = fullPath.toFile();
                
                if (file.exists() && file.isFile()) {
                    Files.delete(fullPath);
                    log.info("文件删除成功: {}", relativePath);
                } else {
                    log.warn("文件不存在: {}", relativePath);
                }
            } else {
                log.warn("无法从 URL 中提取相对路径: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件扩展名（包含点号）
     * 
     * @param filename 文件名
     * @return 扩展名（如 ".jpg"），如果没有扩展名则返回 ".jpg" 作为默认值
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return ".jpg"; // 默认扩展名
    }

    /**
     * 构建相对路径
     * 
     * @param prefix 路径前缀（如 "menu/"）
     * @param dateDir 日期目录（如 "2024/01/15"）
     * @param fileName 文件名（如 "abc123.jpg"）
     * @return 相对路径（如 "menu/2024/01/15/abc123.jpg"）
     */
    private String buildRelativePath(String prefix, String dateDir, String fileName) {
        StringBuilder path = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            path.append(prefix);
            if (!prefix.endsWith("/")) {
                path.append("/");
            }
        }
        path.append(dateDir).append("/").append(fileName);
        return path.toString();
    }

    /**
     * 从访问 URL 中提取相对路径
     * 
     * @param fileUrl 文件的访问 URL（如 "/uploads/menu/2024/01/15/abc123.jpg"）
     * @return 相对路径（如 "menu/2024/01/15/abc123.jpg"），如果无法提取则返回 null
     */
    private String extractRelativePath(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        // 如果 URL 以 accessPath 开头，移除它
        if (fileUrl.startsWith(accessPath)) {
            return fileUrl.substring(accessPath.length());
        }
        
        // 如果 URL 以 / 开头，移除它
        if (fileUrl.startsWith("/")) {
            return fileUrl.substring(1);
        }
        
        return fileUrl;
    }
}


