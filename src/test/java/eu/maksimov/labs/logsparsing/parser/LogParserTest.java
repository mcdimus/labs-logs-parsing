package eu.maksimov.labs.logsparsing.parser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dmitri Maksimov
 */
class LogParserTest {

  private static Collection<LogParser> logParserImplementations() {
    return asList(
        new BufferedReaderLogParser(),
        new StreamLogParser(),
        new ParallelStreamLogParser(),
        new StreamAndParallelMapLogParser(),
        new ForkJoinLogParser(),
        new ExecutorServiceLogParser()
    );
  }

  // TODO: if input is null or empty

  @ParameterizedTest()
  @MethodSource({"logParserImplementations"})
  void parse_Entries_SmallFile(LogParser parser) throws Exception {
    // given
    Path inputLogFile = Paths.get(getClass().getClassLoader().getResource("simple.log").toURI());

    // when
    Set<Entry> result = parser.parse(inputLogFile);

    // then
    assertThat(result).containsOnly(
        new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.049Z")).threadId("http--0.0.0.0-28080-405").userContext("").resource("/checkSession.do").requestDurationMillis(187L).build(),
        new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:01.050Z")).threadId("http--0.0.0.0-28080-405").userContext("CUST:CUS5T27233").resource("/checkSession.do").requestDurationMillis(256L).build(),
        new UriEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:02.814Z")).threadId("http--0.0.0.0-28080-245").userContext("CUST:CUS5T27233").resource("/checkSession.do").queryString("msisdn=300501633574").requestDurationMillis(17L).build(),
        new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("").resource("getSubscriptionAuthTokens").requestDurationMillis(25L).build(),
        new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:00:04.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(singletonList("300553344974")).requestDurationMillis(50L).build(),
        new ResourceEntry.Builder().timestamp(Instant.parse("2015-08-19T00:01:03.376Z")).threadId("http--0.0.0.0-28080-85").userContext("USER:300553344974").resource("getSubscriptionAuthTokens").data(asList("300553344974","300553344975","300553344976")).requestDurationMillis(42L).build()
    );
  }

  @ParameterizedTest()
  @MethodSource({"logParserImplementations"})
  void parse_Entries_1001LinesFile(LogParser parser) throws Exception {
    // given
    Path inputLogFile = Paths.get(getClass().getClassLoader().getResource("timing.log").toURI());

    // when
    Set<Entry> result = parser.parse(inputLogFile);

    // then
    assertThat(result).hasSize(1001);
  }

}
