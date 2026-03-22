package www.rdm.com;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class TextExtractionState {
    final Map<Integer, Map<Integer, Float>> fontSizes = new HashMap<>();
    final Map<Integer, Integer> columnCounts = new HashMap<>();
    final Map<Integer, Map<Integer, String>> textContent = new HashMap<>();
    int currentPage = 0, currentColumn = 0;
    float maxFontSize = 0;
    int chapterStartPage = 0, chapterEndPage = 0;
    String normalizedChapterName = "";

    float getFontSize(int page, int col) {
        return fontSizes.getOrDefault(page, Collections.emptyMap()).getOrDefault(col, 0.0f);
    }

    String getText(int page, int col) {
        return textContent.getOrDefault(page, Collections.emptyMap()).getOrDefault(col, "");
    }

    void putFontSize(int page, int col, float value) {
        fontSizes.computeIfAbsent(page, k -> new HashMap<>()).put(col, value);
    }

    void putText(int page, int col, String value) {
        textContent.computeIfAbsent(page, k -> new HashMap<>()).put(col, value);
    }

    void putColumnCount(int page, int col) {
        columnCounts.put(page, col);
    }

    int getColumnCount(int page) {
        return columnCounts.getOrDefault(page, 0);
    }
}
