package gov.texas.tpwd.mobileranger;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;

import java.io.IOException;
import java.net.MalformedURLException;

import gov.texas.tpwd.mobileranger.pdf.PdfWritable;

public class TreeReportWriter extends PdfWritable {

    private TreeReport mTreeReport;
    private String mFilename;

    public TreeReportWriter(TreeReport treeReport, String filename) {
        mTreeReport = treeReport;
        mFilename = filename;
    }

    @Override
    public void write() {
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
    }

    private void writeTree(Document document) throws DocumentException, MalformedURLException, IOException {
        if(mTreeReport == null) {
            return;
        }

        if(mTreeReport.getDate() != null && !mTreeReport.getDate().isEmpty()) {
            document.add(new Paragraph(mTreeReport.getDate()));
        }

        if(mTreeReport.getReportingEmployee() != null && !mTreeReport.getReportingEmployee().isEmpty()) {
            document.add(new Paragraph(mTreeReport.getReportingEmployee()));
        }

        if(mTreeReport.getLocation() != null && !mTreeReport.getLocation().isEmpty()) {
            document.add(new Paragraph(mTreeReport.getLocation()));
        }

        if(mTreeReport.getDetails() != null && !mTreeReport.getDetails().isEmpty()) {
            document.add(new Paragraph(mTreeReport.getDetails()));
        }

        if(mTreeReport.getActionTaken() != null && !mTreeReport.getActionTaken().isEmpty()) {
            document.add(new Paragraph(mTreeReport.getActionTaken()));
        }

        if(mTreeReport.getBeforeImagePath() != null && !mTreeReport.getBeforeImagePath().isEmpty()) {
            document.add(Image.getInstance(mTreeReport.getBeforeImagePath()));
        }


    }

}
