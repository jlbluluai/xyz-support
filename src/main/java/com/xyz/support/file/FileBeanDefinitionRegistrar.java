package com.xyz.support.file;

import com.xyz.support.file.ftp.FtpFileService;
import com.xyz.support.file.local.LocalFileService;
import com.xyz.support.file.qiniu.QiNiuFileService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 文件服务BeanDefinition注册
 * <p>
 * 注册了BeanDefinition后配合{@link FileBeanFactory}对创建后的bean进行配置
 *
 * @author xyz
 * @date 2021/8/17
 **/
public class FileBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    /**
     * 环境配置（可取得配置文件中的配置）
     */
    private ConfigurableEnvironment environment;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean xyzSupportFileEnable = environment.getProperty("xyz.support.file.enable", Boolean.class, Boolean.FALSE);
        // 若开启了文件服务，检查配置中是否有相关的文件名
        if (xyzSupportFileEnable) {
            // 尝试注册local的BeanDefinition
            int index = 0;
            String serviceName;
            while (StringUtils.hasText(serviceName = environment.getProperty(String.format("xyz.support.file.local[%s].serviceName", index++)))) {
                Assert.isTrue(!registry.containsBeanDefinition(serviceName), String.format("文件服务local服务配置 serviceName-%s 存在冲突的bean", serviceName));
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(LocalFileService.class);
                registry.registerBeanDefinition(serviceName, beanDefinition);
            }

            // 尝试注册ftp的BeanDefinition
            index = 0;
            while (StringUtils.hasText(serviceName = environment.getProperty(String.format("xyz.support.file.ftp[%s].serviceName", index++)))) {
                Assert.isTrue(!registry.containsBeanDefinition(serviceName), String.format("文件服务ftp服务配置 serviceName-%s 存在冲突的bean", serviceName));
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(FtpFileService.class);
                registry.registerBeanDefinition(serviceName, beanDefinition);
            }

            // 尝试注册qiniu的BeanDefinition
            index = 0;
            while (StringUtils.hasText(serviceName = environment.getProperty(String.format("xyz.support.file.qiniu[%s].serviceName", index++)))) {
                Assert.isTrue(!registry.containsBeanDefinition(serviceName), String.format("文件服务七牛云服务配置 serviceName-%s 存在冲突的bean", serviceName));
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(QiNiuFileService.class);
                registry.registerBeanDefinition(serviceName, beanDefinition);
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
