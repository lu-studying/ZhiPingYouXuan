package com.demo.dp.controller;

import com.demo.dp.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器：提供文件上传相关的 REST API 接口
 * 
 * <p>所有接口路径都以 {@code /api/files} 为前缀。
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileStorageService fileStorageService;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Value("${file.upload.max-size}")
    private long maxSize;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * 上传图片文件到本地存储
     * 
     * <p>请求示例：
     * <ul>
     *   <li>POST /api/files/upload</li>
     *   <li>Content-Type: multipart/form-data</li>
     *   <li>Body: file=图片文件, pathPrefix=menu/（可选）</li>
     * </ul>
     * 
     * @param file 上传的文件
     * @param pathPrefix 存储路径前缀（可选，如 "menu/"），如果不提供则使用配置文件中的默认值
     * @return ResponseEntity 包含上传成功后的文件 URL
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pathPrefix", required = false) String pathPrefix) {
        
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return error(400, "文件不能为空");
            }

            // 验证文件类型（只允许图片）
            String contentType = file.getContentType();
            if (contentType == null || !isAllowedType(contentType)) {
                return error(400, "只允许上传图片文件（jpg, jpeg, png, gif, webp）");
            }

            // 验证文件大小
            if (file.getSize() > maxSize) {
                return error(400, "文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
            }

            // 上传文件到本地存储
            String fileUrl = fileStorageService.uploadFile(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    pathPrefix
            );

            // 返回成功响应
            // 注意：返回相对路径，前端需要自己拼接 BASE_URL
            // 例如：/uploads/menu/2024/01/15/abc123.jpg
            // 前端拼接后：http://localhost:8080/uploads/menu/2024/01/15/abc123.jpg
            Map<String, Object> response = new HashMap<>();
            response.put("url", fileUrl); // 相对路径，前端需要拼接 BASE_URL
            response.put("message", "上传成功");
            
            log.info("文件上传成功: {}", fileUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return error(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除本地文件
     * 
     * @param fileUrl 文件的访问 URL 或相对路径
     * @return ResponseEntity 包含删除结果
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("url") String fileUrl) {
        try {
            fileStorageService.deleteFile(fileUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return error(500, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件类型是否允许
     * 
     * @param contentType MIME 类型
     * @return 是否允许
     */
    private boolean isAllowedType(String contentType) {
        if (contentType == null) {
            return false;
        }
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (contentType.equals(type.trim())) {
                return true;
            }
        }
        return false;
    }

    private ResponseEntity<Map<String, Object>> error(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", status);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

