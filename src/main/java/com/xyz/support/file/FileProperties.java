package com.xyz.support.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 文件服务的配置
 * <p>
 * 本地文件服务已支持
 * Ftp文件服务已支持
 * 七牛云文件服务已支持
 *
 * @author xyz
 * @date 2021/8/11
 **/
@ConfigurationProperties(prefix = "xyz.support.file")
@Data
public class FileProperties {

    /**
     * 是否启用file服务，默认不启用，一旦启用但啥也没配置，也等于没启用
     */
    private boolean enable;

    /**
     * 七牛云文件服务的配置
     */
    private List<QiNiuProperties> qiniu;

    /**
     * ftp文件服务的配置
     */
    private List<FtpProperties> ftp;

    /**
     * 本地文件服务的配置
     */
    private List<LocalProperties> local;

    @Data
    public static class LocalProperties {
        private String serviceName;
        private String localPath;

        public void check() {
            Assert.notNull(serviceName, "local prop error, serviceName cannot be null");
            Assert.notNull(localPath, "local prop error, localPath cannot be null");
        }
    }

    @Data
    public static class FtpProperties {
        private String serviceName;
        private String host = "127.0.0.1";
        private int port = 21;
        private int timeout = 5000;
        private String username;
        private String password;

        public void check() {
            Assert.notNull(serviceName, "ftp prop error, serviceName cannot be null");
            Assert.notNull(username, "ftp prop error, username cannot be null");
            Assert.notNull(password, "ftp prop error, password cannot be null");
        }
    }

    @Data
    public static class QiNiuProperties {
        private String serviceName;
        private String accessKey;
        private String secretKey;
        private String bucket;
        private String domain;
        private boolean publicFlag;

        public void check() {
            Assert.notNull(serviceName, "qiniu prop error, serviceName cannot be null");
            Assert.notNull(accessKey, "qiniu prop error, accessKey cannot be null");
            Assert.notNull(secretKey, "qiniu prop error, secretKey cannot be null");
            Assert.notNull(bucket, "qiniu prop error, bucket cannot be null");
            Assert.notNull(domain, "qiniu prop error, domain cannot be null");
        }
    }

}
