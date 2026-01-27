package com.demo.dp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类：配置静态资源访问
 * 
 * 功能说明：
 * 1. 配置本地文件存储的静态资源访问路径
 * 2. 让前端可以直接通过 URL 访问上传的图片
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.base-path}")
    private String basePath;

    @Value("${file.upload.access-path}")
    private String accessPath;

    /**
     * 配置静态资源处理器
     * 
     * 功能说明：
     * 1. 将访问路径（如 /uploads/**）映射到本地文件系统路径
     * 2. 前端可以通过 http://localhost:8080/uploads/xxx.jpg 直接访问图片
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 移除 accessPath 开头的 "/"，因为 Spring 会自动处理
        String pathPattern = accessPath.endsWith("/") 
            ? accessPath + "**" 
            : accessPath + "/**";
        
        // 确保 basePath 以 "/" 结尾（如果是相对路径）或使用绝对路径
        String resourceLocation = basePath.endsWith("/") 
            ? "file:" + basePath 
            : "file:" + basePath + "/";
        
        registry.addResourceHandler(pathPattern)
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600); // 缓存时间（秒）
    }
}


