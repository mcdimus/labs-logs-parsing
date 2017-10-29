package eu.maksimov.labs.logsparsing.model;

import java.time.Instant;
import java.util.Objects;

/**
 * @author Dmitri Maksimov
 */
public class UriEntry extends Entry {

  private String queryString;

  private UriEntry(Builder builder) {
    setTimestamp(builder.timestamp);
    setThreadId(builder.threadId);
    setUserContext(builder.userContext);
    setRequestDurationMillis(builder.requestDurationMillis);
    setResource(builder.resource);
    setQueryString(builder.queryString);
  }

  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    UriEntry uriEntry = (UriEntry) o;
    return Objects.equals(queryString, uriEntry.queryString);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 89 + Objects.hash(queryString);
  }

  @Override
  public String toString() {
    return String.format("%s (%s) [%s] %s?%s in %dms",
        super.getTimestamp(),
        super.getThreadId(),
        super.getUserContext() == null ? "" : super.getUserContext(),
        resource,
        queryString,
        getRequestDurationMillis()
    );
  }

  public static final class Builder {

    private Instant timestamp;
    private String threadId;
    private String userContext;
    private Long requestDurationMillis;

    private String resource;
    private String queryString;

    public Builder() {
    }

    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder threadId(String threadId) {
      this.threadId = threadId;
      return this;
    }

    public Builder userContext(String userContext) {
      this.userContext = userContext;
      return this;
    }

    public Builder requestDurationMillis(Long requestDurationMillis) {
      this.requestDurationMillis = requestDurationMillis;
      return this;
    }

    public Builder resource(String resource) {
      this.resource = resource;
      return this;
    }

    public Builder queryString(String queryString) {
      this.queryString = queryString;
      return this;
    }

    public UriEntry build() {
      return new UriEntry(this);
    }

  }

}
