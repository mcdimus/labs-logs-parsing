package eu.maksimov.labs.logsparsing.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Stream;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;
import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class ForkJoinLogParser implements LogParser {

  @Override
  public Set<Entry> parse(Path logFile) {
    try (Stream<String> stream = Files.lines(logFile)) {
      List<String> lines = stream.collect(toList());

      Set<Entry> entries = Collections.synchronizedSet(new LinkedHashSet<>(lines.size()));
      ForkJoinPool forkJoinPool = new ForkJoinPool();
      ParseAction task = new ParseAction(lines, 0, lines.size(), entries);
      try {
        forkJoinPool.invoke(task);
      } finally {
        forkJoinPool.shutdown();
      }
      return entries;
    } catch (IOException e) {
      throw new IllegalStateException();
    }
  }

  public static class ParseAction extends RecursiveAction {

    public static final EntryParser PARSER = new EntryParserFactory().getInstance();

    public static final int THRESHOLD = 200;

    private List<String> lines;
    private int startIndex;
    private int length;
    private Set<Entry> output;

    public ParseAction(List<String> lines, int startIndex, int length, Set<Entry> output) {
      this.lines = lines;
      this.startIndex = startIndex;
      this.length = length;
      this.output = output;
    }

    @Override
    protected void compute() {
      if (length <= THRESHOLD) {
        computeDirectly();
        return;
      }

      int split = length / 2;

      invokeAll(
          new ParseAction(lines, startIndex, split, output),
          new ParseAction(lines, startIndex + split, length - split, output)
      );
    }

    private void computeDirectly() {
      lines.subList(startIndex, startIndex + length).stream().map(PARSER::parse).forEach(output::add);
    }

  }

}
