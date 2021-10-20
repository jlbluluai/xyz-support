package com.xyz.support.file.qiniu;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.xyz.support.file.AbstractFileService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 七牛云文件服务
 *
 * @author xyz
 * @date 2021/8/10
 **/
@Setter
@Slf4j
public class QiNiuFileService extends AbstractFileService {

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 空间名
     */
    private String bucket;

    /**
     * 空间是否公开
     */
    private boolean publicFlag;

    /**
     * 访问域名
     */
    private String domain;

    /**
     * token本地缓存
     */
    private final LoadingCache<String, String> tokenCache;

    public QiNiuFileService() {
        tokenCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(3600 - 100, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return initToken();
                    }
                });
    }

    /**
     * 获取七牛云的token，
     */
    private String getToken() {
        try {
            return tokenCache.get("qiniu.token");
        } catch (Exception e) {
            throw new RuntimeException("cannot get qiniu token", e);
        }
    }

    /**
     * 初始化token
     */
    private String initToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 通过默认方式获取的token3600s后过期
        String upToken = auth.uploadToken(bucket);
        System.out.println(upToken);
        return upToken;
    }

    @Deprecated
    @Override
    public String upload(File file, String fileName, String filePath) throws Exception {
        throw new RuntimeException("七牛云不支持目录形式存储");
    }

    @Deprecated
    @Override
    public String upload(MultipartFile file, String fileName, String filePath) throws Exception {
        throw new RuntimeException("七牛云不支持目录形式存储");
    }

    @Override
    public String upload(InputStream is, String fileName, String filePath) throws Exception {
        throw new RuntimeException("七牛云不支持目录形式存储");
    }

    @Override
    protected String doUpload(InputStream is, String fileName, String filePath) {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        String upToken = getToken();

        try {
            Response response = uploadManager.put(is, fileName, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return putRet.key;
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException("upload error");
        }
    }

    @Override
    protected InputStream doDownload(String fileName) {
        // 获取下载链接
        String urlString = getDownloadUrl(fileName);

        // 通过链接下载文件
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(urlString).build();
        try (okhttp3.Response response = client.newCall(req).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("download error resp = {}", response.body());
                throw new RuntimeException("download error");
            }
            okhttp3.ResponseBody body = response.body();

            return body.byteStream();
        } catch (Exception e) {
            log.error("download error", e);
            throw new RuntimeException("download error");
        }
    }

    /**
     * 获取下载链接
     *
     * @param fileName 文件名
     * @return 下载链接
     */
    private String getDownloadUrl(String fileName) {
        try {
            DownloadUrl url = new DownloadUrl(domain, false, fileName);
            String urlString;
            if (publicFlag) {
                urlString = url.buildURL();
            } else {
                Auth auth = Auth.create(accessKey, secretKey);
                urlString = url.buildURL(auth, System.currentTimeMillis() / 1000 + 3600);
            }
            return urlString;
        } catch (Exception e) {
            log.error("download getDownloadUrl error", e);
            throw new RuntimeException("download error");
        }
    }
}
