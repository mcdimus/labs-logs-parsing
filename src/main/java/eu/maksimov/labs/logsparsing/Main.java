package eu.maksimov.labs.logsparsing;

import eu.maksimov.labs.logsparsing.histogram.Histogram;
import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.LogParserFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Collections.reverseOrder;

/**
 * @author Dmitri Maksimov
 */
public class Main {

    private Path file;
    private int limit;

    public Main(Path file, int limit) {
        this.file = file;
        this.limit = limit;
    }

    public void run() {
        List<Entry> entries = new LogParserFactory().getInstance().parse(file);
        Stats stats = new Stats(entries);

        printStatistics(stats.getAverageRequestTimePerResource(), limit);
        printHistogram(stats.getHistogramOfHourlyNumberOfRequests());
    }

    private void printStatistics(Map<String, Double> averageRequestTimePerResource, int limit) {
        System.out.println("Top " + (limit == -1 ? "" : limit + " ") + "resources with highest average request duration:");

        Stream<Map.Entry<String, Double>> stream = averageRequestTimePerResource.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(reverseOrder()));

        if (limit >= 0) {
            stream = stream.limit(limit);
        }

        AtomicInteger counter = new AtomicInteger(1);
        stream.forEach(x -> System.out.printf("%d) %s: %.2fms\n", counter.getAndIncrement(), x.getKey(), x.getValue()));
    }

    private void printHistogram(Histogram<Instant> histogramOfHourlyNumberOfRequests) {
        System.out.println("\nHistogram of hourly number of requests:");
        histogramOfHourlyNumberOfRequests.print(System.out);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

//        if (args.length == 0 || args[0].equals("-h")) {
//            printUsage();
//            return;
//        }

//        String filePathArg = args[0];
        Path filePath = Paths.get("C:\\Users\\dmitri\\IdeaProjects\\labs-logs-parsing\\src\\test\\resources\\timing.log");
//        Path filePath;
//        try {
//            filePath = Paths.get(filePathArg);
//            if (!Files.exists(filePath)) {
//                System.err.println("File " + filePath + " does not exist");
//                return;
//            }
//        } catch (InvalidPathException e) {
//            System.err.println("Invalid input for argument <file>.");
//            printUsage();
//            return;
//        }

        int limit = 5; // no limit
        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input for argument <n>.");
                printUsage();
                return;
            }
        }

        new Main(filePath, limit).run();

        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start) + "ms");
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar assignment.jar <file> [<n>]");
        System.out.println("\t <file> - path to log file");
        System.out.println("\t <n> (optional)- print out top n resources with highest average request duration");
    }

}
