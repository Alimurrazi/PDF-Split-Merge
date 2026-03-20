package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class PdfMergeService {
    private static final Logger LOGGER = Logger.getLogger(PdfMergeService.class.getName());

    void merge(List<String> inputPaths, File outputFile) throws Exception {
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            PdfCopy pdfcopy = new PdfCopy(document, fos);
            document.open();
            for (String filepath : inputPaths) {
                PdfReader pdfreader = new PdfReader(filepath);
                try {
                    for (int j = 1; j <= pdfreader.getNumberOfPages(); j++) {
                        try {
                            pdfcopy.addPage(pdfcopy.getImportedPage(pdfreader, j));
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Skipping malformed page " + j + " in " + filepath, e);
                        }
                    }
                } finally {
                    pdfreader.close();
                }
            }
            document.close();
        }
    }
}
