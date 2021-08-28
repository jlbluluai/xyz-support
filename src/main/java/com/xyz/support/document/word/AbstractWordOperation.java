package com.xyz.support.document.word;

/**
 * word operation abstract
 *
 * @author xyz
 * @date 2021/4/26
 */
public abstract class AbstractWordOperation implements WordOperation {

    protected static final String DOC = WordTypeEnum.DOC.getType();
    protected static final String DOCX = WordTypeEnum.DOCX.getType();

    /**
     * get fileType by fileName
     *
     * @param fileName the name of file, default we think the name is complete (contains th suffix)
     * @return the type of File "doc"、"docx"
     */
    protected String parseFileType(String fileName) {
        String[] splits = fileName.split("\\.");
        if (splits.length <= 1) {
            throw new IllegalArgumentException("the fileName:'" + fileName + "' is not correct");
        }

        String fileType = splits[splits.length - 1];

        if (fileType.equalsIgnoreCase(DOC) || fileType.equalsIgnoreCase(DOCX)) {
            return fileType;
        }

        throw new IllegalArgumentException("the suffix of fileName:'" + fileName + "' is not correct");
    }

}
