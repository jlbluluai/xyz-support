package com.xyz.support.file;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 文件服务的配置
 *
 * @author xyz
 * @date 2021/8/11
 **/
@Import(FileBeanDefinitionRegistrar.class)
@EnableConfigurationProperties(FileProperties.class)
public class FileConfiguration {

    @Bean
    public FileBeanFactory fileBeanFactory() {
        return new FileBeanFactory();
    }

}
