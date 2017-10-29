package eu.maksimov.labs.logsparsing.parser;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import eu.maksimov.labs.logsparsing.parser.entry.IndexOfEntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.RegExpEntryParser;
import eu.maksimov.labs.logsparsing.parser.entry.SplitEntryParser;

@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class EntryParserBenchmark {

  private static String URI_ENTRY_STRING = "2015-08-19 00:00:02,814 (http--0.0.0.0-28080-245) [CUST:CUS5T27233] /substypechange.do?msisdn=300501633574 in 17";
  private static String RESOURCE_ENTRY_STRING = "2015-08-19 00:00:11,171 (http--0.0.0.0-28080-3) [CUST:CUS5T27233] updateSubscriptionFromBackend 300407044035 in 206";

  @Benchmark
  public void splitEntryParser_UriEntry(Blackhole blackhole) {
    blackhole.consume(new SplitEntryParser().parse(URI_ENTRY_STRING));
  }

  @Benchmark
  public void splitEntryParser_ResourceEntry(Blackhole blackhole) {
    blackhole.consume(new SplitEntryParser().parse(RESOURCE_ENTRY_STRING));
  }

  @Benchmark
  public void regExpEntryParser_UriEntry(Blackhole blackhole) {
    blackhole.consume(new RegExpEntryParser().parse(URI_ENTRY_STRING));
  }

  @Benchmark
  public void regExpEntryParser_ResourceEntry(Blackhole blackhole) {
    blackhole.consume(new RegExpEntryParser().parse(RESOURCE_ENTRY_STRING));
  }

  @Benchmark
  public void indexOfEntryParser_UriEntry(Blackhole blackhole) {
    blackhole.consume(new IndexOfEntryParser().parse(URI_ENTRY_STRING));
  }

  @Benchmark
  public void indexOfEntryParser_ResourceEntry(Blackhole blackhole) {
    blackhole.consume(new IndexOfEntryParser().parse(RESOURCE_ENTRY_STRING));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(EntryParserBenchmark.class.getSimpleName())
        .build();

    new Runner(opt).run();
  }

}
