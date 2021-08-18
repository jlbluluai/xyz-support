package com.xyz.support.file;

import com.xyz.support.file.ftp.FtpFileService;
import com.xyz.support.file.local.LocalFileService;
import com.xyz.support.file.qiniu.QiNiuFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件服务BeanFactory
 * <p>
 * {@link FileBeanDefinitionRegistrar}已经注册了BeanDefinition，但还没有进行服务配置，该处拿出对应创建好的bean进行服务配置
 *
 * @author xyz
 * @date 2021/8/11
 **/
@Slf4j
public class FileBeanFactory implements BeanFactoryAware {

    @Resource
    private FileProperties fileProperties;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 若非启用 直接return
        if (!fileProperties.isEnable()) {
            return;
        }

        // 若是启用，检查几种配置是否都为空，都为空，也直接return
        if (CollectionUtils.isEmpty(fileProperties.getLocal()) &&
                CollectionUtils.isEmpty(fileProperties.getFtp())
                && CollectionUtils.isEmpty(fileProperties.getQiniu())) {
            return;
        }

        List<String> beanNames = new ArrayList<>();

        // 尝试配置local
        if (!CollectionUtils.isEmpty(fileProperties.getLocal())) {
            List<FileProperties.LocalProperties> localProps = fileProperties.getLocal();
            localProps.forEach(e -> {
                e.check();
                File file = new File(e.getLocalPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                LocalFileService localFileService = beanFactory.getBean(e.getServiceName(), LocalFileService.class);
                localFileService.setLocalPath(e.getLocalPath());
                beanNames.add(e.getServiceName());
            });
        }

        // 尝试配置ftp
        if (!CollectionUtils.isEmpty(fileProperties.getFtp())) {
            List<FileProperties.FtpProperties> ftpProps = fileProperties.getFtp();
            ftpProps.forEach(e -> {
                e.check();
                FtpFileService ftpFileService = beanFactory.getBean(e.getServiceName(), FtpFileService.class);
                ftpFileService.setHost(e.getHost());
                ftpFileService.setPort(e.getPort());
                ftpFileService.setTimeout(e.getTimeout());
                ftpFileService.setUsername(e.getUsername());
                ftpFileService.setPassword(e.getPassword());
                beanNames.add(e.getServiceName());
            });
        }

        // 尝试配置七牛云
        if (!CollectionUtils.isEmpty(fileProperties.getQiniu())) {
            List<FileProperties.QiNiuProperties> qiniuProps = fileProperties.getQiniu();
            qiniuProps.forEach(e -> {
                e.check();
                QiNiuFileService qiNiuFileService = beanFactory.getBean(e.getServiceName(), QiNiuFileService.class);
                qiNiuFileService.setAccessKey(e.getAccessKey());
                qiNiuFileService.setSecretKey(e.getSecretKey());
                qiNiuFileService.setBucket(e.getBucket());
                qiNiuFileService.setPublicFlag(e.isPublicFlag());
                qiNiuFileService.setDomain(e.getDomain());
                beanNames.add(e.getServiceName());
            });
        }

        log.info("gen fileInterface beans, names = {}", beanNames);
    }
}
