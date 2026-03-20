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

            float currentAra = state.getAra(state.compp, state.compl);
            if (curFontSize != currentAra) {
                state.compl = state.compl + 1;
                state.putAra(state.compp, state.compl, curFontSize);
                state.putSara(state.compp, state.compl, text);
                state.putAra1(state.compp, state.compl);
            } else {
                String existing = state.getSara(state.compp, state.compl);
                state.putSara(state.compp, state.compl, existing + text);
                state.putAra1(state.compp, state.compl);
            }

            String currentSara = state.getSara(state.compp, state.compl);
            if (state.chaptername.equals(currentSara)) {
                float currentFontSize = state.getAra(state.compp, state.compl);
                if (currentFontSize > state.max) {
                    state.max = currentFontSize;
                    state.startforinput = state.compp;
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
