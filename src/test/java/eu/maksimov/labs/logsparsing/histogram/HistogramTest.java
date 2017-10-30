package eu.maksimov.labs.logsparsing.histogram;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HistogramTest {

    @Test
    void print_IfMaxValueEqualsToMaxWidth() throws Exception {
        // given
        Map<String, Long> data = new HashMap<>();
        data.put("a", 0L);
        data.put("b", 1L);
        data.put("c", 9L);
        data.put("d", 10L);
        data.put("e", 11L);
        data.put("f", 100L);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name())) {
            // when
            new Histogram<>(data).print(ps);

            // then
            assertThat(new String(baos.toByteArray(), StandardCharsets.UTF_8)).isEqualToNormalizingNewlines(
                    "===================================================\n" +
                            "'#' represents number from 1 to 1\n" +
                            "===================================================\n" +
                            "[a] (  0): \n" +
                            "[b] (  1): #\n" +
                            "[c] (  9): #########\n" +
                            "[d] ( 10): ##########\n" +
                            "[e] ( 11): ###########\n" +
                            "[f] (100): ####################################################################################################\n" +
                            "===================================================\n"
            );
        }
    }

    @Test
    void print_IfMaxValueIsTwiceLargerToMaxWidth() throws Exception {
        // given
        Map<String, Long> data = new HashMap<>();
        data.put("a", 0L);
        data.put("b", 1L);
        data.put("c", 9L);
        data.put("d", 10L);
        data.put("e", 11L);
        data.put("f", 200L);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name())) {
            // when
            new Histogram<>(data).print(ps);

            // then
            assertThat(new String(baos.toByteArray(), StandardCharsets.UTF_8)).isEqualToNormalizingNewlines(
                    "===================================================\n" +
                            "'#' represents number from 1 to 2\n" +
                            "===================================================\n" +
                            "[a] (  0): \n" +
                            "[b] (  1): #\n" +
                            "[c] (  9): #####\n" +
                            "[d] ( 10): #####\n" +
                            "[e] ( 11): ######\n" +
                            "[f] (200): ####################################################################################################\n" +
                            "===================================================\n"
            );
        }
    }

    @Test
    void print_IfMaxValueIsNotDivisibleByMaxWidth() throws Exception {
        // given
        Map<String, Long> data = new HashMap<>();
        data.put("a", 0L);
        data.put("b", 1L);
        data.put("c", 9L);
        data.put("d", 10L);
        data.put("e", 11L);
        data.put("prime1", 233L);
        data.put("primeSecond", 557L);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name())) {
            // when
            new Histogram<>(data).print(ps);

            // then
            assertThat(new String(baos.toByteArray(), StandardCharsets.UTF_8)).isEqualToNormalizingNewlines(
                    "===================================================\n" +
                            "'#' represents number from 1 to 6\n" +
                            "===================================================\n" +
                            "[a          ] (  0): \n" +
                            "[b          ] (  1): #\n" +
                            "[c          ] (  9): ##\n" +
                            "[d          ] ( 10): ##\n" +
                            "[e          ] ( 11): ##\n" +
                            "[prime1     ] (233): #######################################\n" +
                            "[primeSecond] (557): #############################################################################################\n" +
                            "===================================================\n"
            );
        }
    }

}