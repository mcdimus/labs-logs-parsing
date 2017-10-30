package eu.maksimov.labs.logsparsing.parser;

import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class StreamAndParallelMapLogParser implements LogParser {

  @Override
  public List<Entry> parse(Path logFile) {
    EntryParser entryParser = new EntryParserFactory().getInstance();

    try (Stream<String> lines = Files.lines(logFile)) {
      List<String> list = lines.collect(toList());
      return list.parallelStream()
          .map(entryParser::parse)
              .collect(toList());
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

}
