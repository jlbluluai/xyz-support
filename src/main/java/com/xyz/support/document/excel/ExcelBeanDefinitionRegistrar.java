package com.xyz.support.document.excel;

import com.xyz.support.document.excel.poi.DefaultPoiExcelOperation;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel服务BeanDefinition注册
 * <p>
 * 只需要配置服务名，其他都不配置，理论上不出错也会提供poi的默认实现作为服务
 *
 * @author xyz
 * @date 2021/8/22
 **/
public class ExcelBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    /**
     * 环境配置（可取得配置文件中的配置）
     */
    private ConfigurableEnvironment environment;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean enable = environment.getProperty("xyz.support.document.excel.enable", Boolean.class, Boolean.FALSE);
        if (enable) {
            int index = 0;
            String serviceName;
            while (StringUtils.hasText(serviceName = environment.getProperty(String.format("xyz.support.document.excel.bean[%s].serviceName", index++)))) {
                Assert.isTrue(!registry.containsBeanDefinition(serviceName), String.format("Excel服务配置 serviceName-%s 存在冲突的bean", serviceName));

                String type = environment.getProperty(String.format("xyz.support.document.excel.bean[%s].type", index - 1));
                // 若是type未读取到值，按默认提供poi的默认实现作为服务
                ExcelServiceTypeEnum typeEnum;
                if (StringUtils.hasText(type)) {
                    typeEnum = ExcelServiceTypeEnum.resolve(type);
                    Assert.notNull(typeEnum, String.format("Excel服务配置 type-%s配置有误", type));
                } else {
                    typeEnum = ExcelServiceTypeEnum.POI;
                }

                // 若非自定义，取配置的默认，若是自定义，找到指定类加载
                Class<?> target = typeEnum.getDefaultClazz();
                if (typeEnum == ExcelServiceTypeEnum.CUSTOM) {
                    String clazz = environment.getProperty(String.format("xyz.support.document.excel.bean[%s].clazz", index - 1));
                    try {
                        target = Class.forName(clazz);
                        Assert.isTrue(matchParentExists(ExcelOperation.class, target), String.format("Excel服务配置 clazz-%s配置请确保实现ExcelOperation接口", clazz));
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(String.format("Excel服务配置 clazz-%s配置有误", clazz), e);
                    }
                }

                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(target);
                registry.registerBeanDefinition(serviceName, beanDefinition);
            }
        }
    }

    /**
     * 匹配当前类是否继承自某个类（接口
     *
     * @param target 被继承的目标类（接口）
     * @param cur    当前类
     * @return true/false
     */
    private static boolean matchParentExists(Class<?> target, Class<?> cur) {
        if (cur == target) {
            return true;
        }

        Class<?> superclass = cur.getSuperclass();
        if (superclass != null) {
            boolean flag = matchParentExists(target, superclass);
            if (flag) {
                return true;
            }
        }

        Class<?>[] interfaces = cur.getInterfaces();
        if (interfaces.length > 0) {
            for (Class<?> iClass : interfaces) {
                boolean flag = matchParentExists(target, iClass);
                if (flag) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }


    private enum ExcelServiceTypeEnum {

        CUSTOM("custom", null),
        POI("poi", DefaultPoiExcelOperation.class),
        ;


        private final String type;
        private final Class<?> defaultClazz;

        private static Map<String, ExcelServiceTypeEnum> map = Arrays.stream(ExcelServiceTypeEnum.values())
                .collect(Collectors.toMap(ExcelServiceTypeEnum::getType, e -> e));

        ExcelServiceTypeEnum(String type, Class<?> defaultClazz) {
            this.type = type;
            this.defaultClazz = defaultClazz;
        }

        public String getType() {
            return type;
        }

        public Class<?> getDefaultClazz() {
            return defaultClazz;
        }

        public static ExcelServiceTypeEnum resolve(String type) {
            return map.get(type);
        }


    }

}
