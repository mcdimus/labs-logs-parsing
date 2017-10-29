package eu.maksimov.labs.logsparsing.parser;

import java.util.Collection;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.IndexOfEntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.RegExpEntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.SplitEntryParser;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dmitri Maksimov
 */
class EntryParserTest {

  private static Collection<EntryParser> entryParserImplementations() {
    return asList(new SplitEntryParser(), new RegExpEntryParser(), new IndexOfEntryParser());
  }

  // TODO: if input is null or empty

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_UriEntry_IfNoQueryString(EntryParser parser) {
    // given
    String input = "2015-08-19 00:06:33,552 (http--0.0.0.0-28080-101) [CUST:CUS81B3383] /checkSession.do in 121";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(UriEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:06:33.552Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-101");
      softly.assertThat(it.getUserContext()).isEqualTo("CUST:CUS81B3383");
      softly.assertThat(it.getResource()).isEqualTo("/checkSession.do");
      softly.assertThat(it.getQueryString()).isNull();
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(121);
      softly.assertAll();
    });
  }

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_UriEntry_IfHasQueryString(EntryParser parser) {
    // given
    String input = "2015-08-19 00:00:02,814 (http--0.0.0.0-28080-245) [CUST:CUS5T27233] /substypechange.do?msisdn=300501633574 in 17";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(UriEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:00:02.814Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-245");
      softly.assertThat(it.getUserContext()).isEqualTo("CUST:CUS5T27233");
      softly.assertThat(it.getResource()).isEqualTo("/substypechange.do");
      softly.assertThat(it.getQueryString()).isEqualTo("msisdn=300501633574");
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(17);
      softly.assertAll();
    });
  }

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_UriEntry_IfEmptyUserContext(EntryParser parser) {
    // given
    String input = "2015-08-19 00:00:02,814 (http--0.0.0.0-28080-245) [] /substypechange.do?msisdn=300501633574 in 17";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(UriEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:00:02.814Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-245");
      softly.assertThat(it.getUserContext()).isEmpty();
      softly.assertThat(it.getResource()).isEqualTo("/substypechange.do");
      softly.assertThat(it.getQueryString()).isEqualTo("msisdn=300501633574");
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(17);
      softly.assertAll();
    });
  }

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_ResourceEntry_IfNoDataPayload(EntryParser parser) {
    // given
    String input = "2015-08-19 00:04:45,259 (http--0.0.0.0-28080-405) [ASP CUST:CUS5T27233] updateSubscriptionFromBackend in 32";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(ResourceEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:04:45.259Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-405");
      softly.assertThat(it.getUserContext()).isEqualTo("ASP CUST:CUS5T27233");
      softly.assertThat(it.getResource()).isEqualTo("updateSubscriptionFromBackend");
      softly.assertThat(it.getData()).isEmpty();
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(32);
      softly.assertAll();
    });
  }

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_ResourceEntry_IfDataPayloadOfOne(EntryParser parser) {
    // given
    String input = "2015-08-19 00:04:45,259 (http--0.0.0.0-28080-405) [ASP CUST:CUS5T27233] updateSubscriptionFromBackend 300553344974 in 32";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(ResourceEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:04:45.259Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-405");
      softly.assertThat(it.getUserContext()).isEqualTo("ASP CUST:CUS5T27233");
      softly.assertThat(it.getResource()).isEqualTo("updateSubscriptionFromBackend");
      softly.assertThat(it.getData()).containsExactly("300553344974");
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(32);
      softly.assertAll();
    });
  }

  @ParameterizedTest
  @MethodSource({"entryParserImplementations"})
  void parse_ResourceEntry_IfDataPayloadOfMultiple(EntryParser parser) {
    // given
    String input = "2015-08-19 00:04:45,259 (http--0.0.0.0-28080-405) [ASP CUST:CUS5T27233] updateSubscriptionFromBackend 300553344974 300553344975 300553344976 300553344977 in 32";

    // when
    Entry result = parser.parse(input);

    // then
    assertThat(result).isInstanceOfSatisfying(ResourceEntry.class, (it) -> {
      SoftAssertions softly = new SoftAssertions();
      softly.assertThat(it.getTimestamp()).isEqualTo("2015-08-19T00:04:45.259Z");
      softly.assertThat(it.getThreadId()).isEqualTo("http--0.0.0.0-28080-405");
      softly.assertThat(it.getUserContext()).isEqualTo("ASP CUST:CUS5T27233");
      softly.assertThat(it.getResource()).isEqualTo("updateSubscriptionFromBackend");
      softly.assertThat(it.getData()).containsExactly("300553344974", "300553344975", "300553344976", "300553344977");
      softly.assertThat(it.getRequestDurationMillis()).isEqualTo(32);
      softly.assertAll();
    });
  }

}
