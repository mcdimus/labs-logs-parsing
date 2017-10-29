package eu.maksimov.labs.logsparsing.parser.entry;

/**
 * @author Dmitri Maksimov
 */
public class EntryParserFactory {

  public EntryParser getInstance() {
    return new IndexOfEntryParser();
  }

}
