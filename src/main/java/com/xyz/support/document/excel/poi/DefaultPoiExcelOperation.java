package com.xyz.support.document.excel.poi;

import com.xyz.support.document.excel.SheetItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;


/**
 * 默认的Excel使用POI的操作
 * <p>
 * 默认实现中，所有的操作我们都将默认只有一个sheet
 *
 * @author xyz
 * @date 2021/4/13
 */
@Slf4j
public class DefaultPoiExcelOperation extends AbstractDefaultPoiExcelOperation {

    @Override
    public <T> List<T> parse(File file, boolean filterFirstRow, Class<T> resultType) throws Exception {
        Assert.notNull(file, "excel文件必传");
        Assert.notNull(resultType, "解析的结果类型必传");

        // prepare Workbook
        Workbook workbook = createWorkbook(file);
        if (Objects.isNull(workbook)) {
            throw new RuntimeException("无法解析该Excel");
        }

        // 默认认为只有一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        if (Objects.isNull(sheet)) {
            throw new RuntimeException("无法解析Excel的内容");
        }

        // parse sheet
        return parse(sheet, filterFirstRow, resultType);
    }

    @Override
    public <T> List<T> parse(MultipartFile file, boolean filterFirstRow, Class<T> resultType) throws Exception {
        Assert.notNull(file, "excel文件必传");
        Assert.notNull(resultType, "解析的结果类型必传");

        // prepare Workbook
        Workbook workbook = createWorkbook(file);
        if (Objects.isNull(workbook)) {
            throw new RuntimeException("无法解析该Excel");
        }

        // 默认认为只有一个sheet
        Sheet sheet = workbook.getSheetAt(0);
        if (Objects.isNull(sheet)) {
            throw new RuntimeException("无法解析Excel的内容");
        }

        return parse(sheet, filterFirstRow, resultType);
    }

    @Override
    public <T> void export(File target, Class<T> dataType, List<T> dataList) throws Exception {
        // check
        Assert.notNull(target, "导出的目标文件不得为null");
        String excelType = parseExcelType(target.getName());
        Assert.notNull(dataType, "导出解析的数据类型不得为null");

        // build workbook
        Workbook workbook = createWorkbook(excelType);
        buildWorkbook(workbook, dataType, dataList);

        // output
        try (OutputStream out = new FileOutputStream(target)) {
            workbook.write(out);
            out.flush();
        }
    }

    @Override
    public <T> void export(HttpServletResponse response, String fileName, Class<T> dataType, List<T> dataList) throws Exception {
        // check
        Assert.notNull(response, "response不得为null");
        Assert.notNull(fileName, "导出的文件名不得为null");
        String excelType = parseExcelType(fileName);
        Assert.notNull(dataType, "导出解析的数据类型不得为null");

        // build workbook
        Workbook workbook = createWorkbook(excelType);
        buildWorkbook(workbook, dataType, dataList);

        // set response
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        // output
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }
    }

    @Override
    public void export(File target, SheetItem sheetItem) throws Exception {
        // check
        Assert.notNull(target, "导出的目标文件不得为null");
        String excelType = parseExcelType(target.getName());
        Assert.notNull(sheetItem, "导出的数据体不得为null");

        // build workbook
        Workbook workbook = createWorkbook(excelType);
        buildWorkbook(workbook, sheetItem);

        // output
        try (OutputStream out = new FileOutputStream(target)) {
            workbook.write(out);
            out.flush();
        }
    }

    @Override
    public void export(HttpServletResponse response, String fileName, SheetItem sheetItem) throws Exception {
        // check
        Assert.notNull(response, "response不得为null");
        Assert.notNull(fileName, "导出的文件名不得为null");
        String excelType = parseExcelType(fileName);
        Assert.notNull(sheetItem, "导出的数据体不得为null");

        // build workbook
        Workbook workbook = createWorkbook(excelType);
        buildWorkbook(workbook, sheetItem);

        // set response
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // output
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
            out.flush();
        }
    }

}
