package eu.maksimov.labs.logsparsing.parser.entry;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;

/**
 * @author Dmitri Maksimov
 */
public class SplitEntryParser implements EntryParser {

  @Override
  public Entry parse(String entryString) {
    String[] parts = entryString.split("\\s+(?![^\\[(]*[])])"); // Negative Lookahead to ignore spaces between '()' and/or '[]'
    Instant timestamp = getInstant(parts[0], parts[1]);
    String threadId = trimChars(parts[2], 1);
    String userContext = trimChars(parts[3], 1);
    String resource = parts[4];
    Long duration = Long.valueOf(parts[parts.length - 1]);

    if (resource.startsWith("/") && resource.contains(".")) {
      String[] resourceParts = resource.split("\\?");
      String name = resourceParts[0];
      String query = null;
      if (resourceParts.length > 1) {
        query = resourceParts[1];
      }
      return new UriEntry.Builder()
          .timestamp(timestamp)
          .threadId(threadId)
          .userContext(userContext)
          .resource(name)
          .queryString(query)
          .requestDurationMillis(duration)
          .build();
    } else {
      List<String> data = Arrays.asList(parts).subList(5, parts.length - 2);
      return new ResourceEntry.Builder()
          .timestamp(timestamp)
          .threadId(threadId)
          .userContext(userContext)
          .resource(resource)
          .data(data)
          .requestDurationMillis(duration)
          .build();
    }
  }

  private Instant getInstant(String date, String time) {
    return Instant.from(DATE_TIME_FORMATTER.parse(date + " " + time));
  }

  private String trimChars(String part, int numOfChars) {
    return part.substring(numOfChars, part.length() - numOfChars);
  }

}
