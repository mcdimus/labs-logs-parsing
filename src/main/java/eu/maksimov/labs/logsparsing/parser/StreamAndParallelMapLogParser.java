package eu.maksimov.labs.logsparsing.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class StreamAndParallelMapLogParser implements LogParser {

  @Override
  public Set<Entry> parse(Path logFile) {
    EntryParser entryParser = new EntryParserFactory().getInstance();

    try (Stream<String> lines = Files.lines(logFile)) {
      List<String> list = lines.collect(toList());
      return list.parallelStream()
          .map(entryParser::parse)
          .collect(toCollection(LinkedHashSet::new));
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

}
