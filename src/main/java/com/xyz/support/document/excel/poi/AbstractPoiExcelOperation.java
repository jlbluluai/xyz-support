package com.xyz.support.document.excel.poi;

import com.google.common.collect.Lists;
import com.xyz.support.document.excel.AbstractExcelOperation;
import com.xyz.support.document.excel.SheetItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * excel操作关于POI服务的统一抽象服务
 * <p>
 * 包含excel关于POI操作的一般性操作
 *
 * @author xyz
 * @date 2021/4/13
 */
@Slf4j
public abstract class AbstractPoiExcelOperation extends AbstractExcelOperation {

    /**
     * 创建Workbook
     *
     * @param file excel文件
     * @return Workbook
     */
    protected final Workbook createWorkbook(File file) throws IOException {
        // 解析出文件类型
        String fileType = parseExcelType(file.getName());

        // 根据excel类型创建对应的workbook
        Workbook workbook;
        try (InputStream inputStream = new FileInputStream(file)) {
            if (fileType.equalsIgnoreCase(XLS)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileType.equalsIgnoreCase(XLSX)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("the suffix of fileName:'" + file.getName() + "' is not correct");
            }
        } catch (IOException e) {
            throw new IOException(String.format("解析excel，解析 文件-%s 失败", file.getName()), e);
        }
        return workbook;
    }

    /**
     * 创建Workbook
     *
     * @param file excel文件
     * @return Workbook
     */
    protected final Workbook createWorkbook(MultipartFile file) throws IOException {
        // 解析出文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("excel文件名未能解析");
        }
        String fileType = parseExcelType(fileName);

        // 根据excel类型创建对应的workbook
        Workbook workbook;
        try (InputStream inputStream = file.getInputStream()) {
            if (fileType.equalsIgnoreCase(XLS)) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileType.equalsIgnoreCase(XLSX)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("the suffix of fileName:'" + file.getName() + "' is not correct");
            }
        } catch (IOException e) {
            throw new IOException(String.format("解析excel，解析 文件-%s 失败", file.getName()), e);
        }
        return workbook;
    }

    /**
     * 根据excel类型创建Workbook
     *
     * @param excelType excel类型, 支持 "xls"、"xlsx"
     * @return workbook
     */
    protected final Workbook createWorkbook(String excelType) {
        Workbook workbook;
        if (excelType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook();
        } else if (excelType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook();
        } else {
            throw new IllegalArgumentException(String.format("不支持的excel类型：%s", excelType));
        }
        return workbook;
    }

    /**
     * 创建Workbook
     * <p>
     * 通用的关于POI使用{@link SheetItem}构造Workbook
     *
     * @param workbook  Workbook
     * @param sheetItem SheetItem
     */
    protected void buildWorkbook(Workbook workbook, SheetItem sheetItem) {
        Sheet sheet;
        if (StringUtils.hasText(sheetItem.getName())) {
            sheet = workbook.createSheet(sheetItem.getName());
        } else {
            sheet = workbook.createSheet();
        }

        int columnSize = 0;
        // 设置表头
        int rowCounter = 0;
        if (!CollectionUtils.isEmpty(sheetItem.getHeaders())) {
            columnSize = sheetItem.getHeaders().size();
            Row row = sheet.createRow(rowCounter++);
            int headerCellCounter = 0;
            // 头部元素定制
            CellStyle headerCellStyle = createDefaultCellStyle(workbook);
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            for (String header : sheetItem.getHeaders()) {
                Cell cell = row.createCell(headerCellCounter++);
                cell.setCellValue(header);
                cell.setCellStyle(headerCellStyle);
            }
        }

        // 设置表身
        if (!CollectionUtils.isEmpty(sheetItem.getValues())) {
            CellStyle cellStyle = createDefaultCellStyle(workbook);
            for (List<String> value : sheetItem.getValues()) {
                columnSize = Math.max(columnSize, value.size());
                Row row = sheet.createRow(rowCounter++);
                int cellCounter = 0;
                for (String cellValue : value) {
                    Cell cell = row.createCell(cellCounter++);
                    cell.setCellValue(cellValue);
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        // 设置列宽度自动适配
        for (int i = 0; i < columnSize; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    /**
     * 解析sheet
     *
     * @param sheet          表单
     * @param filterFirstRow 判断是否忽略第一行
     * @param resultType     解析后的POJO对象类型
     * @return result list
     */
    protected <T> List<T> parse(Sheet sheet, boolean filterFirstRow, Class<T> resultType) throws Exception {
        // 计算有效的第一行以及实际的行数
        int start = filterFirstRow ? 1 : 0;
        int rowNum = sheet.getPhysicalNumberOfRows();

        // 若不能找到有效的行数 return empty list
        if (rowNum - start <= 0) {
            log.warn("解析excel, 未找到有效行数, resultType = {}", resultType.getName());
            return Collections.emptyList();
        }

        // 遍历解析行
        List<T> dataList = Lists.newArrayList();
        for (int i = start; i < rowNum; i++) {
            Row row = sheet.getRow(i);
            if (Objects.nonNull(row)) {
                T data = parseRow(row, resultType);
                if (Objects.nonNull(data)) {
                    dataList.add(data);
                }
            }
        }

        return dataList;
    }

    /**
     * 解析row
     * <p>
     * 具体解析按什么标准，交给子类
     *
     * @param row        行
     * @param resultType 解析后的POJO对象类型
     * @return result 可能为null
     */
    protected abstract <T> T parseRow(Row row, Class<T> resultType) throws Exception;

    /**
     * 创建默认的元素类型
     *
     * @param workbook Workbook
     * @return CellStyle
     */
    protected CellStyle createDefaultCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        return cellStyle;
    }

}
