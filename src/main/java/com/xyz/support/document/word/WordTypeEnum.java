package com.xyz.support.document.word;

/**
 * word file type
 *
 * @author xyz
 * @date 2021/4/27
 */
public enum WordTypeEnum {

    DOC("doc"),
    DOCX("docx"),
    ;

    private final String type;

    WordTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
