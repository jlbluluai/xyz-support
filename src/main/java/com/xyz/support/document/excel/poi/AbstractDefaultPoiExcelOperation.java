package com.xyz.support.document.excel.poi;

import com.google.common.collect.Lists;
import com.xyz.support.SupportConstant;
import com.xyz.support.document.excel.ExcelCell;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * excel操作关于POI服务的默认抽象服务
 * <p>
 * 该抽象解析对象均参照{@link ExcelCell}
 *
 * @author xyz
 * @date 2021/4/13
 */
@Slf4j
public abstract class AbstractDefaultPoiExcelOperation extends AbstractPoiExcelOperation {

    /**
     * 构造Workbook
     *
     * @param workbook Workbook
     * @param dataType 数据的POJO对象类型
     *                 默认实现解读{@link ExcelCell}
     * @param dataList 数据集合
     */
    protected <T> void buildWorkbook(Workbook workbook, Class<T> dataType, List<T> dataList) throws IllegalAccessException {
        Sheet sheet = workbook.createSheet();

        // 设置表头
        List<Pair<ExcelCell, Field>> pairs = parseField(dataType);
        int rowCounter = 0;
        Row row = sheet.createRow(rowCounter++);
        int headerCellCounter = 0;
        // 头部元素定制
        CellStyle headerCellStyle = createDefaultCellStyle(workbook);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        for (Pair<ExcelCell, Field> pair : pairs) {
            Cell cell = row.createCell(headerCellCounter++);
            cell.setCellValue(pair.getKey().desc());
            cell.setCellStyle(headerCellStyle);
        }

        // 设置表身
        CellStyle cellStyle = createDefaultCellStyle(workbook);
        if (!CollectionUtils.isEmpty(dataList)) {
            for (T t : dataList) {
                row = sheet.createRow(rowCounter++);
                int cellCounter = 0;
                for (Pair<ExcelCell, Field> pair : pairs) {
                    Cell cell = row.createCell(cellCounter++);

                    Field field = pair.getValue();
                    Object o = field.get(t);
                    String value = parseValue(o, field);
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        // 设置列宽度自动适配
        for (int i = 0; i < pairs.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * 导出解析值
     * <p>
     * null将会被转换成空字符串
     * Date将特殊处理
     */
    private String parseValue(Object value, Field field) {
        if (value == null) {
            return SupportConstant.EMPTY_STR;
        }

        // Date类型特殊处理
        if (value instanceof Date) {
            ExcelCell.DateFormat dateFormat = field.getDeclaredAnnotation(ExcelCell.DateFormat.class);
            String format = "yyyyMMdd HH:mm:ss";
            if (dateFormat != null) {
                format = dateFormat.format();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(value);
        }

        return String.valueOf(value);
    }

    /**
     * 解析字段
     *
     * @param clazzType 数据的POJO对象类型
     *                  默认实现解读{@link ExcelCell}
     * @return 排序完的Field
     */
    private <T> List<Pair<ExcelCell, Field>> parseField(Class<T> clazzType) {
        Field[] fields = clazzType.getDeclaredFields();
        Field.setAccessible(fields, true);
        List<Pair<ExcelCell, Field>> sortedFields = Lists.newArrayList();
        for (Field field : fields) {
            ExcelCell excelCell = field.getDeclaredAnnotation(ExcelCell.class);
            if (Objects.isNull(excelCell)) {
                break;
            }
            sortedFields.add(new Pair<>(excelCell, field));
        }
        if (CollectionUtils.isEmpty(sortedFields)) {
            throw new IllegalArgumentException("解析的对象类型未匹配到符合的元素");
        }
        return sortedFields.stream().sorted(Comparator.comparingInt(e -> e.getKey().order())).collect(Collectors.toList());
    }

    /**
     * 默认实现-通过反射和{@link ExcelCell}去解析赋值
     */
    @Override
    protected <T> T parseRow(Row row, Class<T> resultType) throws Exception {
        T target = resultType.newInstance();

        Field[] fields = resultType.getDeclaredFields();
        Field.setAccessible(fields, true);
        List<Pair<ExcelCell, Field>> sortedFields = parseField(resultType);

        // iterate to set field
        int count = 0;
        for (Pair<ExcelCell, Field> fieldPair : sortedFields) {
            Field field = fieldPair.getValue();
            Cell cell = row.getCell(count);
            String value = "";
            if (cell != null) {
                cell.setCellType(Cell.CELL_TYPE_STRING);
                value = cell.getStringCellValue();
            }
            Object val;
            try {
                val = convert2CorrectValue(value, field);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("解析数据类型出错 数据:%s 数据字段:%s 数据类型:%s", value, field.getName(), field.getType()));
            }
            field.set(target, val);
            count++;
        }

        return target;
    }

    /**
     * 转换值到正确的类型
     */
    private Object convert2CorrectValue(String value, Field field) {
        String type = field.getType().getSimpleName();

        Object val = value;
        switch (type) {
            case "byte":
            case "Byte":
                val = Byte.parseByte(value);
                break;
            case "short":
            case "Short":
                val = Short.parseShort(value);
                break;
            case "int":
            case "Integer":
                val = Integer.parseInt(value);
                break;
            case "long":
            case "Long":
                val = Long.parseLong(value);
                break;
            case "float":
            case "Float":
                val = Float.parseFloat(value);
                break;
            case "double":
            case "Double":
                val = Double.parseDouble(value);
                break;
            case "String":
                break;
            case "Date":
                // 若类型是Date 尝试获取@ExcelCell.DateFormat
                ExcelCell.DateFormat dateFormat = field.getDeclaredAnnotation(ExcelCell.DateFormat.class);
                String format = "yyyyMMdd HH:mm:ss";
                if (dateFormat != null) {
                    format = dateFormat.format();
                }
                try {
                    // 尝试把value按excel处理过的日期形式处理，不行再当做普通字符串解析
                    Date date = DateUtil.getJavaDate(Double.parseDouble(value));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    val = simpleDateFormat.parse(simpleDateFormat.format(date));
                } catch (Exception e) {
                    throw new RuntimeException(String.format("解析excel，解析元素%s成Date解析出错，值为%s", field.getName(), value), e);
                }
                break;
            default:
                throw new RuntimeException(String.format("解析excel，不支持元素%s的类型解析", field.getName()));
        }

        return val;
    }

}
