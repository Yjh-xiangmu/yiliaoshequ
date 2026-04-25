package com.hospital.escort.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    // 上传目录（在项目运行目录下的uploads文件夹）
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 上传文件
     * @param file 文件
     * @param subDir 子目录（例如：cert、avatar等）
     * @return 文件访问路径
     */
    public static String uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 创建目录
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dirPath = UPLOAD_DIR + subDir + "/" + dateDir + "/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 保存文件
        String filePath = dirPath + fileName;
        file.transferTo(new File(filePath));

        // 返回访问路径
        return "/" + subDir + "/" + dateDir + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        try {
            File file = new File(UPLOAD_DIR + filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}