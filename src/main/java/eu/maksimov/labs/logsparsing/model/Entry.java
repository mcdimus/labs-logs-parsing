package eu.maksimov.labs.logsparsing.model;

import java.time.Instant;
import java.util.Objects;

/**
 * @author Dmitri Maksimov
 */
public abstract class Entry {

  private Instant timestamp;
  private String threadId;
  private String userContext;
  protected String resource;
  private Long requestDurationMillis;

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getThreadId() {
    return threadId;
  }

  public void setThreadId(String threadId) {
    this.threadId = threadId;
  }

  public String getUserContext() {
    return userContext;
  }

  public void setUserContext(String userContext) {
    this.userContext = userContext;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Long getRequestDurationMillis() {
    return requestDurationMillis;
  }

  public void setRequestDurationMillis(Long requestDurationMillis) {
    this.requestDurationMillis = requestDurationMillis;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof Entry)) {
      return false;
    }
    Entry entry = (Entry) o;
    return Objects.equals(timestamp, entry.timestamp) &&
        Objects.equals(threadId, entry.threadId) &&
        Objects.equals(userContext, entry.userContext) &&
        Objects.equals(resource, entry.resource) &&
        Objects.equals(requestDurationMillis, entry.requestDurationMillis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, threadId, userContext, resource, requestDurationMillis);
  }

}
