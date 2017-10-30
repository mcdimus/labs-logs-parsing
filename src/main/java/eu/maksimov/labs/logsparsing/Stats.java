package eu.maksimov.labs.logsparsing;

import eu.maksimov.labs.logsparsing.histogram.Histogram;
import eu.maksimov.labs.logsparsing.model.Entry;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.*;

public class Stats {

    private List<Entry> entries;

    public Stats(List<Entry> entries) {
        this.entries = entries;
    }

    public Map<String, Double> getAverageRequestTimePerResource() {
        return entries.stream().collect(
                groupingBy(Entry::getResource, averagingLong(Entry::getRequestDurationMillis))
        );
    }

    public Histogram<Instant> getHistogramOfHourlyNumberOfRequests() {
        Map<Instant, Long> histogramData = entries.stream().collect(
                groupingBy(x -> x.getTimestamp().truncatedTo(HOURS), TreeMap::new, counting())
        );
        return new Histogram<>(histogramData);
    }

}
