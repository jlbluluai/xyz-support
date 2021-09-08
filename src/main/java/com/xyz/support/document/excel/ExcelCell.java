package com.xyz.support.document.excel;

import java.lang.annotation.*;

/**
 * excel表单元素
 *
 * @author xyz
 * @date 2021/4/13
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelCell {

    /**
     * 序列号, 从 0,1... 保证单调递增就行
     */
    int order();

    /**
     * 元素描述 解析时只做展示，导出时作为列标题
     */
    String desc() default "";

    /**
     * excel表单元素日期格式
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface DateFormat {

        String format() default "yyyyMMdd HH:mm:ss";

    }

}
