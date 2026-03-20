package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class PdfSplitService {
    private static final Logger LOGGER = Logger.getLogger(PdfSplitService.class.getName());

    void split(String inputPath, List<int[]> pageRanges, File outputFile) throws Exception {
        Document document = new Document();
        PdfReader pdfreader = null;
        try {
            pdfreader = new PdfReader(inputPath);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                PdfCopy copy = new PdfCopy(document, fos);
                document.open();
                for (int[] range : pageRanges) {
                    for (int j = range[0]; j <= range[1]; j++) {
                        try {
                            copy.addPage(copy.getImportedPage(pdfreader, j));
                        } catch (BadPdfFormatException ex) {
                            LOGGER.log(Level.WARNING, "Skipping malformed page " + j, ex);
                        }
                    }
                }
                document.close();
            }
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }
}
