package eu.maksimov.labs.logsparsing.parser;

import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class ForkJoinLogParser implements LogParser {

  @Override
  public List<Entry> parse(Path logFile) {
    try (Stream<String> stream = Files.lines(logFile)) {
      List<String> lines = stream.collect(toList());

      List<Entry> entries = new ArrayList<>(lines.size());
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
    private List<Entry> output;

    public ParseAction(List<String> lines, int startIndex, int length, List<Entry> output) {
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
