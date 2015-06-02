package gov.texas.tpwd.mobileranger.pdf;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class PdfWritable {

    Document mDocument;

    public abstract void write();

    protected Document openDocument(String filename) {
        if(mDocument == null) {
            mDocument = new Document();
        }

        try {
            PdfWriter.getInstance(mDocument, new FileOutputStream(filename));
            mDocument.open();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mDocument;

    }

}
