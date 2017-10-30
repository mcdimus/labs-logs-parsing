package eu.maksimov.labs.logsparsing.parser;

import eu.maksimov.labs.logsparsing.model.Entry;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Dmitri Maksimov
 */
public interface LogParser {

  List<Entry> parse(Path logFile);

}
