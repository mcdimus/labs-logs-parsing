package eu.maksimov.labs.logsparsing.parser.entry;

import java.time.format.DateTimeFormatter;
import eu.maksimov.labs.logsparsing.model.Entry;
import static java.time.ZoneOffset.UTC;

/**
 * @author Dmitri Maksimov
 */
public interface EntryParser {

  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss,SSS").withZone(UTC);

  Entry parse(String entryString);

}
