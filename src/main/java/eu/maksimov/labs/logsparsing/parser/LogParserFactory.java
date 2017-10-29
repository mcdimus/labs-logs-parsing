package eu.maksimov.labs.logsparsing.parser;

/**
 * @author Dmitri Maksimov
 */
public class LogParserFactory {

  public LogParser getInstance() {
    return new ForkJoinLogParser();
  }

}
