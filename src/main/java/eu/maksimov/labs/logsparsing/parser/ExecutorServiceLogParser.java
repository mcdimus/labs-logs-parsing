package eu.maksimov.labs.logsparsing.parser;

import eu.maksimov.labs.logsparsing.model.Entry;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.EntryParserFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Dmitri Maksimov
 */
public class ExecutorServiceLogParser implements LogParser {

    @Override
    public List<Entry> parse(Path logFile) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newWorkStealingPool();

        List<Entry> entries = new ArrayList<>();

        List<Future<List<Entry>>> futures = new ArrayList<>(availableProcessors);
        try (Stream<String> stream = Files.lines(logFile)) {
            List<String> lines = stream.collect(toList());
            int batch = lines.size() / availableProcessors;
            for (int i = 0; i < lines.size(); i += batch) {
                Future<List<Entry>> future = executorService.submit(
                        new Task(new ArrayList<>(lines.subList(i, i + Math.min(batch, lines.size() - i))))
                );
                futures.add(future);
            }

            for (Future<List<Entry>> future : futures) {
                entries.addAll(future.get());
            }
            return entries;
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new IllegalStateException();
        } finally {
            executorService.shutdownNow();
        }
    }

    private static class Task implements Callable<List<Entry>> {

        private static final EntryParser PARSER = new EntryParserFactory().getInstance();

        private List<String> lines;

        public Task(List<String> lines) {
            this.lines = lines;
        }

        @Override
        public List<Entry> call() throws Exception {
            return lines.stream().map(PARSER::parse).collect(toList());
        }

    }

}
