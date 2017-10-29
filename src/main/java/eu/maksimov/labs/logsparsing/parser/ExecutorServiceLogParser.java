package eu.maksimov.labs.logsparsing.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;
import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class ExecutorServiceLogParser implements LogParser {

//  private static final ExecutorService executorService = Executors.newWorkStealingPool();

  @Override
  public Set<Entry> parse(Path logFile) {
    int availableProcessors = 4;//Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newWorkStealingPool();//newFixedThreadPool(availableProcessors);

    Set<Entry> entries = new LinkedHashSet<>();

    List<Future<List<Entry>>> futures = new ArrayList<>(availableProcessors);
//    long a= 0;
    try (Stream<String> stream = Files.lines(logFile)) {
      List<String> lines = stream.collect(toList());
//      a = System.currentTimeMillis();
      int batch = lines.size() / availableProcessors;
      for (int i = 0; i < lines.size(); i += batch) {
//        Future<List<Entry>> future = executorService.submit(new Task(lines, i, Math.min(batch, lines.size() - i)));
        Future<List<Entry>> future = executorService.submit(new Task2(new ArrayList<>(lines.subList(i, i + Math.min(batch, lines.size() - i)))));
        futures.add(future);
      }
//      long start = System.currentTimeMillis();

      for (Future<List<Entry>> future : futures) {
        entries.addAll(future.get());
      }
//      long end = System.currentTimeMillis();
//      System.out.println("Waiting has finished in " + (end - start) + "ms");
      return entries;
    } catch (IOException | InterruptedException | ExecutionException e) {
      throw new IllegalStateException();
    } finally {
//      long start = System.currentTimeMillis();
      executorService.shutdownNow();
//      long end = System.currentTimeMillis();
//      System.out.println("Shutdown has finished in " + (end - start) + "ms");
//
//      long b = System.currentTimeMillis();
//      System.out.println("parse: " + (b-a));
    }
  }

  public static class Task implements Callable<List<Entry>> {

    public static final EntryParser PARSER = new EntryParserFactory().getInstance();

    private List<String> lines;
    private int startIndex;
    private int length;

    public Task(List<String> lines, int startIndex, int length) {
      this.lines = lines;
      this.startIndex = startIndex;
      this.length = length;
    }

    @Override
    public List<Entry> call() throws Exception {
//      long start = System.currentTimeMillis();
      List<Entry> collect = lines.subList(startIndex, startIndex + length).parallelStream().map(PARSER::parse).collect(toList());
//      long end = System.currentTimeMillis();
//      System.out.println(Thread.currentThread().getName() + " (size = " + length + " ) has finished in " + (end - start) + "ms");
      return collect;
    }
  }
  public static class Task2 implements Callable<List<Entry>> {

    public static final EntryParser PARSER = new EntryParserFactory().getInstance();

    private List<String> lines;

    public Task2(List<String> lines) {
      this.lines = lines;
    }

    @Override
    public List<Entry> call() throws Exception {
//      long start = System.currentTimeMillis();
      List<Entry> collect = lines.stream().map(PARSER::parse).collect(toList());
//      long end = System.currentTimeMillis();
//      System.out.println(Thread.currentThread().getName() + " (size = " + lines.size() + ") has finished in " + (end - start) + "ms");
      return collect;
    }
  }

}
