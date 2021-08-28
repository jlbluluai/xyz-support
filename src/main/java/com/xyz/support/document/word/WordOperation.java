package com.xyz.support.document.word;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * word操作接口
 * <p>
 * 迁移老代码，暂不提供服务化配置，可以按老的方式以utils中指定注入的方式直接调用
 *
 * @author xyz
 * @date 2021/4/26
 */
public interface WordOperation {

    /**
     * will export one word, the content's layout is like table
     * <p>
     * the rows we will calculate by contents.length and cols
     * <p>
     * we suggest that you should consider the content's max length to set cols number,
     * and the recommended size is 2
     *
     * @param title    if you don't need this, please give null(not blank!!!)
     * @param contents the content will be filled up into table
     * @param cols     the amount of table's col
     * @param fileName the name of file, support "doc"、"docx", if not end with "doc"、"docx", you will get exception
     * @param response HttpServletResponse
     * @throws Exception ex
     */
    void exportLikeTable(@Nullable String title, @NonNull List<String> contents, int cols,
                         @NonNull String fileName, @NonNull HttpServletResponse response) throws Exception;


    /**
     * will export one word, the content's layout is like table
     * <p>
     * the rows we will calculate by contents.length and cols
     * <p>
     * we suggest that you should consider the content's max length to set cols number,
     * and the recommended size is 2
     *
     * @param title      if you don't need this, please give null(not blank!!!)
     * @param contents   the content will be filled up into table
     * @param cols       the amount of table's col
     * @param targetFile the target file, support "doc"、"docx", if not end with "doc"、"docx", you will get exception
     * @throws Exception ex
     */
    void exportLikeTable(@Nullable String title, @NonNull List<String> contents, int cols,
                         @NonNull File targetFile) throws Exception;


}
