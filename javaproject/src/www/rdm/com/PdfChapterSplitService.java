package www.rdm.com;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

class PdfChapterSplitService {
    private static final Logger LOGGER = Logger.getLogger(PdfChapterSplitService.class.getName());

    /**
     * Scans the PDF for a chapter by name and returns its [startPage, endPage],
     * or null if the chapter is not found.
     */
    int[] findChapter(String inputPath, String chapterName) throws IOException {
        TextExtractionState state = new TextExtractionState();
        state.normalizedChapterName = chapterName.replaceAll("\\s+", "").toLowerCase();

        PdfReader reader = new PdfReader(inputPath);
        try {
            SemTextExtractionStrategy strategy = new SemTextExtractionStrategy(state);
            File tempFile = File.createTempFile("pdf_chapter", ".txt");
            try (PrintWriter out = new PrintWriter(new FileOutputStream(tempFile))) {
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    state.currentPage = i;
                    state.currentColumn = 0;
                    out.println(PdfTextExtractor.getTextFromPage(reader, i, (TextExtractionStrategy) strategy));
                }
                out.flush();
            } finally {
                tempFile.delete();
            }
        } finally {
            reader.close();
        }

        if (state.chapterStartPage == 0) {
            return null;
        }

        // Find end of chapter: next heading at the same font size
        boolean found = false;
        for (int i = state.chapterStartPage + 1; i <= state.currentPage; i++) {
            if (found) break;
            for (int j = 1; j <= state.getColumnCount(i); j++) {
                if (state.getFontSize(i, j) == state.maxFontSize) {
                    state.chapterEndPage = i - 1;
                    found = true;
                    break;
                }
            }
        }

        return new int[]{state.chapterStartPage, state.chapterEndPage};
    }

    void extractPages(String inputPath, int from, int to, File outputFile) throws Exception {
        Document document = new Document();
        PdfReader pdfreader = null;
        try {
            pdfreader = new PdfReader(inputPath);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                PdfCopy copy = new PdfCopy(document, fos);
                document.open();
                for (int i = from; i <= to; i++) {
                    try {
                        copy.addPage(copy.getImportedPage(pdfreader, i));
                    } catch (BadPdfFormatException ex) {
                        LOGGER.log(Level.WARNING, "Skipping malformed page " + i, ex);
                    }
                }
                document.close();
            }
        } finally {
            if (pdfreader != null) pdfreader.close();
        }
    }
}
