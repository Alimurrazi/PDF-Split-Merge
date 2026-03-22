package www.rdm.com;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class SemTextExtractionStrategy implements TextExtractionStrategy {
    private static final Logger LOGGER = Logger.getLogger(SemTextExtractionStrategy.class.getName());

    private final TextExtractionState state;
    private String text;

    SemTextExtractionStrategy(TextExtractionState state) {
        this.state = state;
    }

    @Override
    public String getResultantText() {
        return text;
    }

    @Override
    public void beginTextBlock() {
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
        try {
            text = renderInfo.getText();
            Vector curBaseline = renderInfo.getBaseline().getStartPoint();
            Vector topRight = renderInfo.getAscentLine().getEndPoint();
            Rectangle rect = new Rectangle(curBaseline.get(0), curBaseline.get(1), topRight.get(0), topRight.get(1));
            float curFontSize = rect.getHeight();

            text = text.replaceAll("\\s+", "");
            text = text.toLowerCase();

            float existingFontSize = state.getFontSize(state.currentPage, state.currentColumn);
            if (curFontSize != existingFontSize) {
                state.currentColumn = state.currentColumn + 1;
                state.putFontSize(state.currentPage, state.currentColumn, curFontSize);
                state.putText(state.currentPage, state.currentColumn, text);
                state.putColumnCount(state.currentPage, state.currentColumn);
            } else {
                String existing = state.getText(state.currentPage, state.currentColumn);
                state.putText(state.currentPage, state.currentColumn, existing + text);
                state.putColumnCount(state.currentPage, state.currentColumn);
            }

            String currentText = state.getText(state.currentPage, state.currentColumn);
            if (state.normalizedChapterName.equals(currentText)) {
                float currentFontSize = state.getFontSize(state.currentPage, state.currentColumn);
                if (currentFontSize > state.maxFontSize) {
                    state.maxFontSize = currentFontSize;
                    state.chapterStartPage = state.currentPage;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing text render info", e);
        }
    }

    @Override
    public void endTextBlock() {
    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
    }
}
