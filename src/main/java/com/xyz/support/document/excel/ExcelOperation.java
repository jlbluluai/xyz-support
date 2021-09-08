package com.xyz.support.document.excel;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * excel操作接口
 *
 * @author xyz
 * @date 2021/4/13
 */
public interface ExcelOperation {

    /**
     * 解析excel
     * <p>
     * 默认忽略第一行
     *
     * @param file       excel文件 只支持xls、xlsx后缀 cannot be null
     * @param resultType 解析后的POJO对象类型 cannot be null
     *                   如何对应解析可制定，默认的实现均解读{@link ExcelCell}
     * @return 解析后的POJO对象列表，若为空会返回空列表，不会返回null
     */
    <T> List<T> parse(File file, Class<T> resultType) throws Exception;

    /**
     * 解析excel
     *
     * @param file           excel文件 只支持xls、xlsx后缀 cannot be null
     * @param filterFirstRow 判断是否过滤excel第一行 true/false
     *                       若为true，第一行解析时会被忽略掉
     * @param resultType     解析后的POJO对象类型 cannot be null
     *                       如何对应解析可制定，默认的实现均解读{@link ExcelCell}
     * @return 解析后的POJO对象列表，若为空会返回空列表，不会返回null
     */
    <T> List<T> parse(File file, boolean filterFirstRow, Class<T> resultType) throws Exception;

    /**
     * 解析excel（MultipartFile）
     * <p>
     * 默认忽略第一行
     *
     * @param file       excel文件 只支持xls、xlsx后缀 cannot be null
     * @param resultType 解析后的POJO对象类型 cannot be null
     *                   如何对应解析可制定，默认的实现均解读{@link ExcelCell}
     * @return 解析后的POJO对象列表，若为空会返回空列表，不会返回null
     */
    <T> List<T> parse(MultipartFile file, Class<T> resultType) throws Exception;

    /**
     * 解析excel（MultipartFile）
     *
     * @param file           excel文件 只支持xls、xlsx后缀 cannot be null
     * @param filterFirstRow 判断是否过滤excel第一行 true/false
     *                       若为true，第一行解析时会被忽略掉
     * @param resultType     解析后的POJO对象类型 cannot be null
     *                       如何对应解析可制定，默认的实现均解读{@link ExcelCell}
     * @return 解析后的POJO对象列表，若为空会返回空列表，不会返回null
     */
    <T> List<T> parse(MultipartFile file, boolean filterFirstRow, Class<T> resultType) throws Exception;

    /**
     * 导出excel-到目标文件-通过解析POJO对象出数据
     *
     * @param target   目标文件，文件必须是"xls"或"xlsx"格式 cannot be null
     * @param dataType 数据的POJO对象类型 cannot be null
     *                 默认实现解读{@link ExcelCell}
     * @param dataList 数据集合，若未传或集合空导出的excel将会没有数据
     */
    <T> void export(File target, Class<T> dataType, List<T> dataList) throws Exception;


    /**
     * 导出excel-网络传输-通过解析POJO对象出数据
     *
     * @param response Http Servlet 响应 cannot be null
     * @param fileName 导出的文件名，若不以"xls"或"xlsx"结尾，将自动补齐，请注意 cannot be null
     * @param dataType 数据的POJO对象类型 cannot be null
     *                 默认实现解读{@link ExcelCell}
     * @param dataList 数据集合，若未传或集合空导出的excel将会没有数据
     */
    <T> void export(HttpServletResponse response, String fileName, Class<T> dataType, List<T> dataList) throws Exception;

    /**
     * 导出excel-到目标文件-通过通用赋值元素对象
     *
     * @param target    目标文件，文件必须是"xls"或"xlsx"格式 cannot be null
     * @param sheetItem 通用表单内容 cannot be null
     *                  将不强制内容赋值，若未赋值，只能得到一张空excel
     */
    void export(File target, SheetItem sheetItem) throws Exception;

    /**
     * 导出excel-网络传输-通过通用赋值元素对象
     *
     * @param response  Http Servlet 响应 cannot be null
     * @param fileName  导出的文件名，若不以"xls"或"xlsx"结尾，将自动补齐，请注意 cannot be null
     * @param sheetItem 通用表单内容 cannot be null
     *                  将不强制内容赋值，若未赋值，只能得到一张空excel
     */
    void export(HttpServletResponse response, String fileName, SheetItem sheetItem) throws Exception;

}
