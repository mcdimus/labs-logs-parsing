package eu.maksimov.labs.logsparsing;

import eu.maksimov.labs.logsparsing.histogram.Histogram;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class StatsTest {

    @Test
    void getAverageRequestTimePerResource() {
        // given
        List<Entry> entries = new ArrayList<>();
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.049Z")).threadId("http--0.0.0.0-28080-405").userContext("").resource("/checkSession.do").requestDurationMillis(187L).build());
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.050Z")).threadId("http--0.0.0.0-28080-405").userContext("CUST:CUS5T27233").resource("/checkSession.do").requestDurationMillis(256L).build());
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:02.814Z")).threadId("http--0.0.0.0-28080-245").userContext("CUST:CUS5T27233").resource("/checkSession.do").queryString("msisdn=300501633574").requestDurationMillis(17L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("").resource("getSubscriptionAuthTokens").requestDurationMillis(25L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:04.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(singletonList("300553344974")).requestDurationMillis(50L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:01:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(asList("300553344974", "300553344975", "300553344976")).requestDurationMillis(42L).build());

        // when
        Map<String, Double> result = new Stats(entries).getAverageRequestTimePerResource();

        // then
        assertThat(result).containsOnly(
                entry("/checkSession.do", 153.33333333333334),
                entry("getSubscriptionAuthTokens", 39.0)
        );
    }

    @Test
    void getHistogramOfHourlyNumberOfRequests() throws Exception {
        List<Entry> entries = new ArrayList<>();
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.049Z")).threadId("http--0.0.0.0-28080-405").userContext("").resource("/checkSession.do").requestDurationMillis(187L).build());
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.050Z")).threadId("http--0.0.0.0-28080-405").userContext("CUST:CUS5T27233").resource("/checkSession.do").requestDurationMillis(256L).build());
        entries.add(new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:02.814Z")).threadId("http--0.0.0.0-28080-245").userContext("CUST:CUS5T27233").resource("/checkSession.do").queryString("msisdn=300501633574").requestDurationMillis(17L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("").resource("getSubscriptionAuthTokens").requestDurationMillis(25L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:04.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(singletonList("300553344974")).requestDurationMillis(50L).build());
        entries.add(new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:01:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(asList("300553344974", "300553344975", "300553344976")).requestDurationMillis(42L).build());

        // when
        Histogram<Instant> result = new Stats(entries).getHistogramOfHourlyNumberOfRequests();

        // then
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8.name())) {
            // when
            result.print(ps);

            // then
            assertThat(new String(baos.toByteArray(), StandardCharsets.UTF_8)).isEqualToNormalizingNewlines(
                    "===================================================\n" +
                            "'#' represents number from 1 to 1\n" +
                            "===================================================\n" +
                            "[2015-08-19T00:00:00Z] (6): ######\n" +
                            "===================================================\n"
            );
        }
    }

}