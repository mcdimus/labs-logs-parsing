package eu.maksimov.labs.logsparsing.parser;

/**
 * @author Dmitri Maksimov
 */
public class LogParserFactory {

    public LogParser getInstance() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (availableProcessors > 1) {
            return new ExecutorServiceLogParser();
        } else {
            // if there is only one processor no need in thread managing overhead
            return new StreamLogParser();
        }
    }

}
