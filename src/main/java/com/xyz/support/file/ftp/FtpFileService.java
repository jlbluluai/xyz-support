package com.xyz.support.file.ftp;

import com.xyz.support.SupportConstant;
import com.xyz.support.file.AbstractFileService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Random;

/**
 * ftp文件服务
 *
 * @author xyz
 * @date 2021/8/11
 **/
@Slf4j
@Setter
public class FtpFileService extends AbstractFileService {

    /**
     * host
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * 超时时间 单位：毫秒
     */
    private int timeout;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    @Override
    protected String doUpload(InputStream is, String fileName, String filePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.setConnectTimeout(timeout);
            ftpClient.connect(host, port);
            ftpClient.login(username, password);

            // 定位文件目录
            if (StringUtils.hasText(filePath) && !ftpClient.changeWorkingDirectory(filePath)) {
                if (ftpClient.makeDirectory(filePath)) {
                    ftpClient.changeWorkingDirectory(filePath);
                } else {
                    log.error("ftp create filePath fail");
                    throw new RuntimeException("upload error");
                }
            }

            //开启被动模式，客户端传东西到服务端
            ftpClient.enterLocalPassiveMode();
            //开启二进制传输，防止图片损坏
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            if (!ftpClient.storeFile(fileName, is)) {
                log.error("ftp storeFile fail");
                throw new RuntimeException("upload error");
            }
            ftpClient.logout();
        } catch (IOException e) {
            log.error("ftp upload error", e);
            throw new RuntimeException("upload error");
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("ftpClient disconnect error", e);
            }
        }
        return fileName;
    }

    @Override
    protected InputStream doDownload(String fileName) {
        // 拆解得到路径和文件名
        String path = "";
        String name = "";
        int index = fileName.lastIndexOf(SupportConstant.FILE_DELIMITER);
        if (index != -1) {
            path = fileName.substring(0, index + 1);
            name = fileName.substring(index + 1);
        }

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.setConnectTimeout(timeout);
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            if (!"".equals(path)) {
                ftpClient.changeWorkingDirectory(path);
            }

            String tmpFileName = System.currentTimeMillis() + "" + new Random().nextInt(100000);
            File tmpFile = new File(tmpFileName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile ftpFile : files) {
                if (ftpFile.getName().equals(name)) {
                    try (OutputStream os = new FileOutputStream(tmpFile)) {
                        ftpClient.retrieveFile(fileName, os);
                    }
                    break;
                }
            }
            ftpClient.logout();

            FileInputStream is = new FileInputStream(tmpFileName);
            tmpFile.delete();
            return is;
        } catch (IOException e) {
            log.error("file download error", e);
            throw new RuntimeException("download error");
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("ftpClient disconnect error", e);
            }
        }
    }

}
