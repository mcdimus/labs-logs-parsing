package eu.maksimov.labs.logsparsing.parser;

import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitri Maksimov
 */
public class BufferedReaderLogParser implements LogParser {

  @Override
  public List<Entry> parse(Path logFile) {
    EntryParser entryParser = new EntryParserFactory().getInstance();
    List<Entry> entries = new ArrayList<>();

    try (BufferedReader br = Files.newBufferedReader(logFile)) {
      String line;
      while ((line = br.readLine()) != null) {
        if (!line.isEmpty()) {
          entries.add(entryParser.parse(line));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return entries;
  }

}
