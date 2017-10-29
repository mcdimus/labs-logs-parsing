package eu.maksimov.labs.logsparsing.parser.entry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.model.ResourceEntry;
import eu.maksimov.labs.logsparsing.model.UriEntry;

/**
 * @author Dmitri Maksimov
 */
public class IndexOfEntryParser implements EntryParser {

  @Override
  public Entry parse(String entryString) {
    int startOfTimestampIndex = 0;
    int endOfTimestampIndex = entryString.indexOf('(') - 1;
    String timestampString = entryString.substring(startOfTimestampIndex, endOfTimestampIndex);
    Instant timestamp = Instant.from(DATE_TIME_FORMATTER.parse(timestampString));

    int startOfThreadIdIndex = endOfTimestampIndex + 2;
    int endOfThreadIdIndex = entryString.indexOf(')', startOfTimestampIndex);
    String threadId = entryString.substring(startOfThreadIdIndex, endOfThreadIdIndex);

    int startOfUserContextIndex = endOfThreadIdIndex + 3;
    int endOfUserContextIndex = entryString.indexOf(']', startOfUserContextIndex);
    String userContext = entryString.substring(startOfUserContextIndex, endOfUserContextIndex);

    int startOfDurationIndex = entryString.lastIndexOf(' ') + 1;
    Long duration = Long.valueOf(entryString.substring(startOfDurationIndex));

    int startOfResourceIndex = endOfUserContextIndex + 2;
    int endOfResourceIndex = entryString.lastIndexOf(' ') - 3;
    String resource = entryString.substring(startOfResourceIndex, endOfResourceIndex);

    if (resource.charAt(0) == '/') {
      String uri;
      String query = null;

      int startOfQueryIndex = resource.indexOf('?');
      if (startOfQueryIndex == -1) {
        uri = resource;
      } else {
        uri = resource.substring(0, startOfQueryIndex);
        query = resource.substring(startOfQueryIndex + 1);
      }

      return new UriEntry.Builder()
          .timestamp(timestamp)
          .threadId(threadId)
          .userContext(userContext)
          .resource(uri)
          .queryString(query)
          .requestDurationMillis(duration)
          .build();
    } else {
      int startOfDataIndex = resource.indexOf(' ');
      String name;
      List<String> data = new ArrayList<>();
      if (startOfDataIndex == -1) {
        name = resource;
      } else {
        name = resource.substring(0, startOfDataIndex);
        int endOfDataIndex;
        do {
          endOfDataIndex = resource.indexOf(' ', startOfDataIndex + 1);
          if (endOfDataIndex == -1) {
            data.add(resource.substring(startOfDataIndex + 1));
          } else {
            String substring = resource.substring(startOfDataIndex + 1, endOfDataIndex);
            data.add(substring);
            startOfDataIndex = endOfDataIndex;
          }
        } while (endOfDataIndex != -1);
      }

      return new ResourceEntry.Builder()
          .timestamp(timestamp)
          .threadId(threadId)
          .userContext(userContext)
          .resource(name)
          .data(data)
          .requestDurationMillis(duration)
          .build();
    }

  }

}
