package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class PdfPageRemoveService {
    private static final Logger LOGGER = Logger.getLogger(PdfPageRemoveService.class.getName());

    void removePages(String inputPath, List<int[]> pageRanges, File outputFile) throws Exception {
        Document document = new Document();
        PdfReader pdfreader = null;
        try {
            pdfreader = new PdfReader(inputPath);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                PdfCopy copy = new PdfCopy(document, fos);
                document.open();

                Set<Integer> pagesToRemove = new HashSet<>();
                for (int[] range : pageRanges) {
                    for (int j = range[0]; j <= range[1]; j++) {
                        pagesToRemove.add(j);
                    }
                }

                for (int i = 1; i <= pdfreader.getNumberOfPages(); i++) {
                    if (!pagesToRemove.contains(i)) {
                        try {
                            copy.addPage(copy.getImportedPage(pdfreader, i));
                        } catch (BadPdfFormatException ex) {
                            LOGGER.log(Level.WARNING, "Skipping malformed page " + i, ex);
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
