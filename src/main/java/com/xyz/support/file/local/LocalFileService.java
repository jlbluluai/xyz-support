package com.xyz.support.file.local;

import com.xyz.support.SupportConstant;
import com.xyz.support.file.AbstractFileService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 本地文件服务
 * <p>
 * 维护单个文件路径，单主机模式若没有额外资源可以考虑使用
 *
 * @author xyz
 * @date 2021/8/11
 **/
@Slf4j
@Setter
public class LocalFileService extends AbstractFileService {

    private String localPath;

    private String getAbsolutePath(String fileName) {
        return localPath + SupportConstant.FILE_DELIMITER + fileName;
    }

    @Override
    protected String doUpload(InputStream is, String fileName, String filePath) {
        // 若路径存在处理
        String absoluteFileName;
        if (StringUtils.hasText(filePath)) {
            File outFile = new File(filePath);
            outFile = new File(localPath + outFile.getAbsolutePath());
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            absoluteFileName = outFile.getAbsolutePath() + SupportConstant.FILE_DELIMITER + fileName;
        } else {
            absoluteFileName = getAbsolutePath(fileName);
        }
        try (FileOutputStream fos = new FileOutputStream(absoluteFileName)) {
            IOUtils.copy(is, fos);
            fos.flush();
            return fileName;
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    protected InputStream doDownload(String fileName) {
        String path =genDownloadPath(fileName);
        try {
            return new FileInputStream(path);
        } catch (Exception e) {
            log.error("download error", e);
            throw new RuntimeException("download error");
        }
    }

    private String genDownloadPath(String fileName) {
        String path;
        if (fileName.startsWith(SupportConstant.FILE_DELIMITER)) {
            path = localPath + fileName;
        } else {
            path = localPath + SupportConstant.FILE_DELIMITER + fileName;
        }
        return path;
    }

}
