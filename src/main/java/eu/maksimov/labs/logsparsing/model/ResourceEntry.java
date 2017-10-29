package eu.maksimov.labs.logsparsing.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dmitri Maksimov
 */
public class ResourceEntry extends Entry {

  private List<String> data;

  private ResourceEntry(Builder builder) {
    setTimestamp(builder.timestamp);
    setThreadId(builder.threadId);
    setUserContext(builder.userContext);
    setRequestDurationMillis(builder.requestDurationMillis);
    setResource(builder.resource);
    data = builder.data;
  }

  public List<String> getData() {
    if (data == null) {
      data = new ArrayList<>();
    }
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    ResourceEntry that = (ResourceEntry) o;
    return Objects.equals(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + Objects.hash(getData());
  }

  @Override
  public String toString() {
    return String.format("%s (%s) [%s] %s %s in %dms",
        super.getTimestamp(),
        super.getThreadId(),
        super.getUserContext() == null ? "" : super.getUserContext(),
        resource,
        String.join(" ", getData()),
        getRequestDurationMillis()
    );
  }

  public static final class Builder {

    private Instant timestamp;
    private String threadId;
    private String userContext;
    private Long requestDurationMillis;

    private String resource;
    private List<String> data;

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

    public Builder data(List<String> data) {
      this.data = new ArrayList<>(data);
      return this;
    }

    public ResourceEntry build() {
      return new ResourceEntry(this);
    }

  }

}
