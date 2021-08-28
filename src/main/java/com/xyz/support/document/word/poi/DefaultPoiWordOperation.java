package com.xyz.support.document.word.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * default word operation-poi
 *
 * @author xyz
 * @date 2021/4/26
 */
@Slf4j
public class DefaultPoiWordOperation extends AbstractPoiWordOperation {


    @Override
    public void exportLikeTable(@Nullable String title, @NonNull List<String> contents, int cols,
                                @NonNull String fileName, @NonNull HttpServletResponse response) throws Exception {
        // set response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Expires:", "0");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try {
            // check
            Assert.isTrue(!CollectionUtils.isEmpty(contents), "contents cannot be empty");
            Assert.notNull(fileName, "fileName cannot be null");
            Assert.notNull(response, "response cannot be null");
            parseFileType(fileName);

            // build
            XWPFDocument document = buildLikeTable(title, contents, cols);

            // output
            OutputStream out = response.getOutputStream();
            document.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("export error, fileName={}", fileName, e);
            throw new IOException("export error");
        }
    }

    @Override
    public void exportLikeTable(@Nullable String title, @NonNull List<String> contents, int cols,
                                @NonNull File targetFile) throws Exception {
        try {
            // check
            Assert.isTrue(!CollectionUtils.isEmpty(contents), "contents cannot be empty");
            Assert.notNull(targetFile, "targetFile cannot be null");
            parseFileType(targetFile.getName());

            // build
            XWPFDocument document = buildLikeTable(title, contents, cols);

            // output
            OutputStream out = new FileOutputStream(targetFile);
            document.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("export error, fileName={}", targetFile.getName(), e);
            throw new IOException("export error");
        }
    }

    /**
     * build likeTable's XWPFDocument
     */
    protected XWPFDocument buildLikeTable(String title, List<String> contents, int cols) {
        XWPFDocument document = new XWPFDocument();

        // set title
        if (Objects.nonNull(title)) {
            title(document, title);
            wrap(document);
        }

        // set table
        int len = contents.size();
        int rows = len % cols + len / cols;

        XWPFTable infoTable = document.createTable(rows, cols);
        // hide table border
        infoTable.getCTTbl().getTblPr().unsetTblBorders();
        // set the interval between the top and bottom of the element
        infoTable.setCellMargins(150, 0, 0, 0);

        // auto segmentation
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(9072));

        // set element
        int count = 0;
        for (int i = 0; i < rows; i++) {
            XWPFTableRow infoTableRow = infoTable.getRow(i);
            for (int j = 0; j < cols; j++) {
                infoTableRow.getCell(j).setText(contents.get(count++));
                // the end row perhaps redundant, in advance finish
                if (count == len) {
                    break;
                }
            }
        }

        return document;
    }

}
