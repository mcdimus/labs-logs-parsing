package eu.maksimov.labs.logsparsing.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import static java.util.stream.Collectors.toCollection;

@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class LogParserBenchmark {

  private Path logFile;

  @Setup
  public void getFile() throws Exception {
    logFile = Paths.get(getClass().getClassLoader().getResource("timing_large.log").toURI());
  }

  @Benchmark
  public void baseline(Blackhole blackhole) throws Exception {
    try (Stream<String> lines = Files.lines(logFile)) {
      blackhole.consume(lines.collect(toCollection(LinkedHashSet::new)));
    }
  }

  @Benchmark
  public void baseline_count(Blackhole blackhole) throws Exception {
    try (Stream<String> lines = Files.lines(logFile)) {
      blackhole.consume(lines.count());
    }
  }

  @Benchmark
  public void bufferedReaderLogParser(Blackhole blackhole) {
    blackhole.consume(new BufferedReaderLogParser().parse(logFile));
  }

  @Benchmark
  public void streamLogParser(Blackhole blackhole) {
    blackhole.consume(new StreamLogParser().parse(logFile));
  }

  @Benchmark
  public void parallelStreamLogParser(Blackhole blackhole) {
    blackhole.consume(new ParallelStreamLogParser().parse(logFile));
  }

  @Benchmark
  public void streamAndParallelMapLogParser(Blackhole blackhole) {
    blackhole.consume(new StreamAndParallelMapLogParser().parse(logFile));
  }

  @Benchmark
  public void forkJoinLogParser(Blackhole blackhole) {
    blackhole.consume(new ForkJoinLogParser().parse(logFile));
  }

  @Benchmark
  public void executorServiceLogParser(Blackhole blackhole) {
    blackhole.consume(new ExecutorServiceLogParser().parse(logFile));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(LogParserBenchmark.class.getSimpleName())
        .build();

    new Runner(opt).run();
  }

}
