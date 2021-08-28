package com.xyz.support.document.excel;

import lombok.Data;

import java.util.List;

/**
 * Excel Sheet元素
 *
 * @author xyz
 * @date 2021/4/14
 */
@Data
public class SheetItem {

    /**
     * Sheet名 为空则默认
     */
    private String name;

    /**
     * 表头 按顺序排列
     */
    private List<String> headers;

    /**
     * 表身数据 按顺序排列
     */
    private List<List<String>> values;

}
