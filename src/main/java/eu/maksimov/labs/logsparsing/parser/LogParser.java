package eu.maksimov.labs.logsparsing.parser;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import eu.maksimov.labs.logsparsing.model.Entry;

/**
 * @author Dmitri Maksimov
 */
public interface LogParser {

  Set<Entry> parse(Path logFile);

}
