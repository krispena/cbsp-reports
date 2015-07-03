package gov.texas.tpwd.mobileranger;


import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.net.MalformedURLException;

import gov.texas.tpwd.mobileranger.pdf.PdfWritable;

public class TreeReportWriter extends PdfWritable {

    private TreeReport mTreeReport;
    private String mFilename;
    private String title;
    private Font boldFont;
    private Font normalFont;
    private static final int ROW_HEIGHT = 90;
    private static final int TITLE_PADDING = 18;
    private static final int TITLE_FONT = 18;
    private static final int IMAGE_WIDTH = 250;
    private static final int IMAGE_HEIGHT = 150;


    public TreeReportWriter(TreeReport treeReport, String filename, String title) {
        mTreeReport = treeReport;
        mFilename = filename;
        this.title = title;
        boldFont = new Font();
        boldFont.setStyle(Font.BOLD);
        normalFont = new Font();
        normalFont.setStyle(Font.NORMAL);
    }

    @Override
    public String write() {
        Document document = openDocument(mFilename);
        try {
            writeTree(document);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();

        return mFilename;
    }

    private void writeTree(Document document) throws DocumentException, MalformedURLException, IOException {
        if(mTreeReport == null) {
            return;
        }

        Paragraph title = createTitleParagraph();
        document.add(title);

        Paragraph dummy = new Paragraph("\u00a0");
        dummy.setLeading(TITLE_PADDING);
        document.add(dummy);

        PdfPTable titleTable = new PdfPTable(new float[]{1f, 3f});
        titleTable.setWidthPercentage(100f);
        getParagraph(titleTable, "Date", mTreeReport.getDate(), 0);
        getParagraph(titleTable, "Reporting Employee", mTreeReport.getReportingEmployee(), 0);
        document.add(titleTable);

        PdfPTable table = createTableForLocation();

        document.add(table);

    }

    private PdfPTable createTableForLocation() throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(new float[]{1f, 3f});
        table.setWidthPercentage(100f);


        getParagraph(table, "Location", mTreeReport.getLocation(), 0);
        getParagraph(table, "Details", mTreeReport.getDetails(), ROW_HEIGHT);
        getParagraph(table, "Action Taken", mTreeReport.getActionTaken(), ROW_HEIGHT);
        addImageRow(table, "Before Photo", "After Photo");

        return table;
    }

    private void addImageRow(PdfPTable table, String beforeText, String afterText) throws DocumentException, IOException {
        PdfPTable innerTable = new PdfPTable(2);
        addImageTitle(innerTable, beforeText);
        addImageTitle(innerTable, afterText);
        getImageCell(innerTable, mTreeReport.getBeforeImagePath());
        getImageCell(innerTable, mTreeReport.getAfterImagePath());

        PdfPCell cell = new PdfPCell(innerTable);
        cell.setColspan(2);
        table.addCell(cell);
    }

    private void getParagraph(PdfPTable table, String introText, String content, float minimumHeight) {
        Chunk titleChunk = getBoldChunk(introText);
        Chunk contentChunk = getContentChunkIfAvailable(content);
        PdfPCell titleCell = new PdfPCell(new Phrase(titleChunk));
        PdfPCell contentCell = new PdfPCell(new Phrase(contentChunk));
        if(minimumHeight > 0) {
            titleCell.setMinimumHeight(minimumHeight);
            contentCell.setMinimumHeight(minimumHeight);
        }
        table.addCell(titleCell);
        table.addCell(contentCell);
    }

    private void addImageTitle(PdfPTable table, String title) {
        PdfPCell titleCell = new PdfPCell(new Paragraph(title, boldFont));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(titleCell);
    }

    private void getImageCell(PdfPTable table, String imagePath) throws DocumentException, MalformedURLException , IOException {
       // PdfPTable innerTable = new PdfPTable(1);
        //innerTable.addCell(new Paragraph(header, boldFont));
        PdfPCell cell;
        if(imagePath != null && !imagePath.isEmpty()) {
            Image image = Image.getInstance(imagePath);
            image.scaleToFit(IMAGE_WIDTH, IMAGE_HEIGHT);
            cell = new PdfPCell(image);
            cell.setPadding(5);
          //  innerTable.addCell(cell);
        } else {
            cell = new PdfPCell(new Phrase(""));
            cell.setMinimumHeight(50);
            table.addCell(cell);
        }
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private Paragraph createTitleParagraph() {

        Font font = new Font();
        font.setSize(TITLE_FONT);
        font.setStyle(Font.BOLD);
        Phrase phrase = new Phrase(title, font);
        Paragraph title = new Paragraph(phrase);
        title.setAlignment(Element.ALIGN_CENTER);

        return title;
    }

    private Chunk getBoldChunk(String introText) {
        return new Chunk(introText + ": ", boldFont);
    }

    private Chunk getContentChunkIfAvailable(String content) {
        Chunk chunk;
        if(content != null && !content.isEmpty()) {
            chunk = new Chunk(content, normalFont);
        } else {
            chunk = new Chunk();
        }
        return chunk;
    }



}
