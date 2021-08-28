package com.xyz.support.document.word.poi;

import com.xyz.support.document.word.AbstractWordOperation;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * word operation-poi-abstract
 *
 * @author xyz
 * @date 2021/4/26
 */
public abstract class AbstractPoiWordOperation extends AbstractWordOperation {

    protected void title(XWPFDocument document, String title) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText(title);
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(20);
    }

    protected void wrap(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText("\r");
    }


}
