package www.rdm.com;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class TextExtractionState {
    final Map<Integer, Map<Integer, Float>> ara = new HashMap<>();
    final Map<Integer, Integer> ara1 = new HashMap<>();
    final Map<Integer, Map<Integer, String>> sara = new HashMap<>();
    int compp = 0, compl = 0;
    float max = 0;
    int startforinput = 0, endforinput = 0;
    String chaptername = "";

    float getAra(int page, int col) {
        return ara.getOrDefault(page, Collections.emptyMap()).getOrDefault(col, 0.0f);
    }

    String getSara(int page, int col) {
        return sara.getOrDefault(page, Collections.emptyMap()).getOrDefault(col, "");
    }

    void putAra(int page, int col, float value) {
        ara.computeIfAbsent(page, k -> new HashMap<>()).put(col, value);
    }

    void putSara(int page, int col, String value) {
        sara.computeIfAbsent(page, k -> new HashMap<>()).put(col, value);
    }

    void putAra1(int page, int col) {
        ara1.put(page, col);
    }

    int getAra1(int page) {
        return ara1.getOrDefault(page, 0);
    }
}
