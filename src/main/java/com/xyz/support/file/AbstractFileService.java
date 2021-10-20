package com.xyz.support.file;

import com.xyz.support.SupportConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 文件抽象服务
 *
 * @author xyz
 * @date 2021/8/13
 **/
@Slf4j
public abstract class AbstractFileService implements FileInterface {

    @Override
    public String upload(File file) throws Exception {
        Assert.notNull(file, "文件不得为null");
        try (InputStream is = new FileInputStream(file)) {
            return doUpload(is, file.getName(), "");
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(File file, String fileName) throws Exception {
        // 退化处理
        if (!StringUtils.hasText(fileName)) {
            return upload(file);
        }
        Assert.notNull(file, "文件不得为null");

        // 若未携带扩展名根据原文件计算得出
        if (!existFileExtension(fileName)) {
            String extension = getFileExtension(file.getName());
            if (!SupportConstant.EMPTY_STR.equals(extension)) {
                fileName = fileName + SupportConstant.DOT + extension;
            }
        }

        try (InputStream is = new FileInputStream(file)) {
            return doUpload(is, fileName, "");
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(File file, String fileName, String filePath) throws Exception {
        Assert.notNull(file, "文件不得为null");
        // 退化处理
        if (!StringUtils.hasText(fileName)) {
            fileName = file.getName();
        } else {
            // 若未携带扩展名根据原文件计算得出
            if (!existFileExtension(fileName)) {
                String extension = getFileExtension(file.getName());
                if (!SupportConstant.EMPTY_STR.equals(extension)) {
                    fileName = fileName + SupportConstant.DOT + extension;
                }
            }
        }

        // 处理文件路径
        if (StringUtils.hasText(filePath) && !filePath.startsWith(SupportConstant.FILE_DELIMITER)) {
            filePath = SupportConstant.FILE_DELIMITER + filePath;
        }

        try (InputStream is = new FileInputStream(file)) {
            return doUpload(is, fileName, filePath);
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        Assert.notNull(file, "文件不得为null");
        try (InputStream is = file.getInputStream()) {
            return doUpload(is, file.getOriginalFilename(), "");
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws Exception {
        // 退化处理
        if (!StringUtils.hasText(fileName)) {
            return upload(file);
        }
        Assert.notNull(file, "文件不得为null");

        // 若未携带扩展名根据原文件计算得出
        if (!existFileExtension(fileName)) {
            String extension = getFileExtension(file.getOriginalFilename());
            if (!SupportConstant.EMPTY_STR.equals(extension)) {
                fileName = fileName + SupportConstant.DOT + extension;
            }
        }

        try (InputStream is = file.getInputStream()) {
            return doUpload(is, fileName, "");
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(MultipartFile file, String fileName, String filePath) throws Exception {
        Assert.notNull(file, "文件不得为null");
        // 退化处理
        if (!StringUtils.hasText(fileName)) {
            fileName = file.getName();
        } else {
            // 若未携带扩展名根据原文件计算得出
            if (!existFileExtension(fileName)) {
                String extension = getFileExtension(file.getOriginalFilename());
                if (!SupportConstant.EMPTY_STR.equals(extension)) {
                    fileName = fileName + SupportConstant.DOT + extension;
                }
            }
        }

        // 处理文件路径
        if (StringUtils.hasText(filePath) && !filePath.startsWith(SupportConstant.FILE_DELIMITER)) {
            filePath = SupportConstant.FILE_DELIMITER + filePath;
        }

        try (InputStream is = file.getInputStream()) {
            return doUpload(is, fileName, filePath);
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    public String upload(InputStream is, String fileName) throws Exception {
        Assert.notNull(is, "流不得为null");
        Assert.hasText(fileName, "文件名不得为空");

        return doUpload(is, fileName, "");
    }

    @Override
    public String upload(InputStream is, String fileName, String filePath) throws Exception {
        // 退化处理
        if (!StringUtils.hasText(filePath)) {
            return upload(is, fileName);
        }
        Assert.notNull(is, "流不得为null");
        Assert.hasText(fileName, "文件名不得为空");

        // 处理文件路径
        if (StringUtils.hasText(filePath) && !filePath.startsWith(SupportConstant.FILE_DELIMITER)) {
            filePath = SupportConstant.FILE_DELIMITER + filePath;
        }

        return doUpload(is, fileName, filePath);
    }

    /**
     * 以流的方式统一执行上传
     *
     * @param is       流
     * @param fileName 文件名 若有扩展名带上
     * @param filePath 文件路径 存在就处理，不存在忽略
     * @return 文件名（带扩展名）
     */
    protected abstract String doUpload(InputStream is, String fileName, String filePath);


    @Override
    public File download(String fileName, String filePath) throws Exception {
        Assert.notNull(fileName, "文件名不得为null");

        // 建立本地临时文件
        if (filePath == null) {
            filePath = "";
        }
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(filePath + fileName);

        try (InputStream is = doDownload(fileName);
             OutputStream os = new FileOutputStream(file)) {
            IOUtils.copy(is, os);
            os.flush();
            return file;
        } catch (Exception e) {
            log.error("download error", e);
            throw new RuntimeException("download error");
        }
    }

    @Override
    public byte[] download(String fileName) throws Exception {
        Assert.notNull(fileName, "文件名不得为null");

        try (InputStream is = doDownload(fileName)) {
            byte[] data = IOUtils.toByteArray(is);
            return data;
        } catch (Exception e) {
            log.error("download error", e);
            throw new RuntimeException("download error");
        }
    }

    /**
     * 以返回内容输入流的方式统一执行下载
     *
     * @param fileName 文件名
     * @return 内容输入流 请注意调用处关闭获取的流
     */
    protected abstract InputStream doDownload(String fileName);
}
