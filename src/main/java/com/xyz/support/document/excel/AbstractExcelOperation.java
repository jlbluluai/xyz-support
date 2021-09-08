package com.xyz.support.document.excel;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * excel操作统一抽象服务
 * <p>
 * 包含excel操作的一般性操作
 *
 * @author xyz
 * @date 2021/4/13
 */
public abstract class AbstractExcelOperation implements ExcelOperation {

    protected static final String XLS = "xls";
    protected static final String XLSX = "xlsx";

    /**
     * 解析excel的文件类型
     *
     * @param fileName 文件名称
     * @return "xls"、"xlsx"
     */
    protected String parseExcelType(String fileName) {
        String[] splits = fileName.split("\\.");
        if (splits.length <= 1) {
            throw new IllegalArgumentException(String.format("excel文件-%s 无法解析出excel类型", fileName));
        }

        String fileType = splits[splits.length - 1];

        if (fileType.equalsIgnoreCase(XLS) || fileType.equalsIgnoreCase(XLSX)) {
            return fileType;
        }

        throw new IllegalArgumentException(String.format("excel文件-%s 无法解析出类型", fileName));
    }

    /**
     * 由于该方法区别{@link ExcelOperation#parse(File, boolean, Class)}只是filterFirstRow默认为true，
     * 所以一般性抽象服务中直接实现转向该方法，一般来说无需改动
     */
    @Override
    public <T> List<T> parse(File file, Class<T> resultType) throws Exception {
        return parse(file, true, resultType);
    }

    @Override
    public <T> List<T> parse(MultipartFile file, Class<T> resultType) throws Exception {
        return parse(file, true, resultType);
    }
}
