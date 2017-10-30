package eu.maksimov.labs.logsparsing.histogram;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Histogram<T extends Comparable> {

    private static final int MAX_WIDTH = 100;
    private static final char SYMBOL = '#';
    private static final String SEPARATOR_LINE = "===================================================";

    private Map<T, Long> data;

    public Histogram(Map<T, Long> data) {
        this.data = new TreeMap<>(data);
    }

    public void print(PrintStream out) {
        long maxCount = Collections.max(data.values());
        double scale = Math.ceil(maxCount / (double) MAX_WIDTH);
        int keyWidth = data.keySet().stream().mapToInt(x -> x.toString().length()).max().orElse(1);
        int valueWidth = Long.toString(maxCount).length();

        out.println(SEPARATOR_LINE);
        out.printf("'%s' represents number from 1 to %.0f\n", SYMBOL, scale);
        out.println(SEPARATOR_LINE);
        for (Map.Entry<T, Long> entry : data.entrySet()) {
            out.printf("[%-" + keyWidth + "s] (%" + valueWidth + "d): ", entry.getKey().toString(), entry.getValue());
            long symbolCount = (long) Math.ceil(entry.getValue() / scale);
            for (int i = 0; i < symbolCount; i++) {
                out.print(SYMBOL);
            }
            out.println();
        }
        out.println(SEPARATOR_LINE);
    }

}
