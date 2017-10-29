package eu.maksimov.labs.logsparsing.parser.entry;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;
import static java.util.Collections.emptyList;

/**
 * @author Dmitri Maksimov
 */
public class RegExpEntryParser implements EntryParser {

  public static final Pattern PATTERN = Pattern.compile("(?<date>[\\d,.: \\-]+) " +
      "\\((?<threadId>.+)\\) " +
      "\\[(?<userContext>.*?)] " +
      "((?<uri>/.*?)(\\?(?<query>.+?))? |(?<resource>[^\\s]+?) (?<data>.+)?)" +
      "in (?<duration>\\d+)");

  @Override
  public Entry parse(String entryString) {
    Matcher matcher = PATTERN.matcher(entryString);

    if (matcher.find()) {
      Instant timestamp = Instant.from(DATE_TIME_FORMATTER.parse(matcher.group("date")));
      String threadId = matcher.group("threadId");
      String userContext = matcher.group("userContext");
      Long duration = Long.valueOf(matcher.group("duration"));
      String uri = matcher.group("uri");

      if (uri != null) {
        return new UriEntry.Builder()
            .timestamp(timestamp)
            .threadId(threadId)
            .userContext(userContext)
            .resource(uri)
            .queryString(matcher.group("query"))
            .requestDurationMillis(duration)
            .build();
      } else {
        List<String> data = emptyList();
        if (matcher.group("data") != null) {
          data = Arrays.asList(matcher.group("data").split(" "));
        }
        return new ResourceEntry.Builder()
            .timestamp(timestamp)
            .threadId(threadId)
            .userContext(userContext)
            .resource(matcher.group("resource"))
            .data(data)
            .requestDurationMillis(duration)
            .build();
      }
    }
    throw new IllegalArgumentException();
  }

}
