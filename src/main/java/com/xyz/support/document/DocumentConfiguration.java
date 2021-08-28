package com.xyz.support.document;

import com.xyz.support.document.excel.ExcelBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

/**
 * 文档服务配置
 *
 * @author xyz
 * @date 2021/8/22
 **/
@Import(ExcelBeanDefinitionRegistrar.class)
public class DocumentConfiguration {
}
