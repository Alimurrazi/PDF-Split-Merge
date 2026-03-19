package www.rdm.com;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

class SemTextExtractionStrategy extends Chapter implements TextExtractionStrategy {
    private static final Logger LOGGER = Logger.getLogger(SemTextExtractionStrategy.class.getName());
    private String text;

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

            float currentAra = ara.getOrDefault(compp, Collections.emptyMap()).getOrDefault(compl, 0.0f);
            if (curFontSize != currentAra) {
                compl = compl + 1;
                ara.computeIfAbsent(compp, k -> new java.util.HashMap<>()).put(compl, curFontSize);
                sara.computeIfAbsent(compp, k -> new java.util.HashMap<>()).put(compl, text);
                ara1.put(compp, compl);
            } else {
                String existing = sara.getOrDefault(compp, Collections.emptyMap()).getOrDefault(compl, "");
                sara.computeIfAbsent(compp, k -> new java.util.HashMap<>()).put(compl, existing + text);
                ara1.put(compp, compl);
            }

            String currentSara = sara.getOrDefault(compp, Collections.emptyMap()).getOrDefault(compl, "");
            if (chaptername.equals(currentSara)) {
                float currentFontSize = ara.getOrDefault(compp, Collections.emptyMap()).getOrDefault(compl, 0.0f);
                if (currentFontSize > max) {
                    max = currentFontSize;
                    startforinput = compp;
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
