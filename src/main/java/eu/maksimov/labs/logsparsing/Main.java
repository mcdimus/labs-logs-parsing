package eu.maksimov.labs.logsparsing;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.LogParser;
import eu.maksimov.labs.logsparsing.parser.LogParserFactory;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.averagingLong;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author Dmitri Maksimov
 */
public class Main {

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    Path filePath = Paths.get("/home/dmitri/IdeaProjects/labs-logs-parsing/src/test/resources/timing_large.log");
    int n = 10;
    LogParser parser = new LogParserFactory().getInstance();

    long startParsing = System.currentTimeMillis();
    Set<Entry> parse = parser.parse(filePath);
    long endParsing = System.currentTimeMillis();
    System.out.println("Parsing time: " + (endParsing - startParsing) + "ms");

    parse.stream().collect(groupingBy(Entry::getResource,averagingLong(Entry::getRequestDurationMillis)))
        .entrySet().stream().sorted(Map.Entry.comparingByValue(reverseOrder())).limit(n).forEach(System.out::println);
    long end = System.currentTimeMillis();

    System.out.println("Time elapsed: " + (end - start) + "ms");
  }

}
