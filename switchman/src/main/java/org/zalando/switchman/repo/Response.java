package org.zalando.switchman.repo;

public class Response {

    public static Response createSuccessfulResponse() {
        return new Response(Status.SUCCESSFUL);
    }

    public static Response createFailedResponse(final Cause cause) {
        return new Response(Status.FAILED, cause);
    }

    public static Response createSkippedResponse() {
        return new Response(Status.SKIPPED);
    }

    private final Status status;
    private final Cause cause;

    private Response(final Status status) {
        this(status, null);
    }

    private Response(final Status status, final Cause cause) {
        this.status = status;
        this.cause = cause;
    }

    public boolean isSuccessful() {
        return status == Status.SUCCESSFUL;
    }

    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }

    public Cause getCause() {
        return cause;
    }

    private enum Status {
        SUCCESSFUL,
        FAILED,
        SKIPPED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (status != response.status) return false;
        return cause != null ? cause.equals(response.cause) : response.cause == null;

    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", cause=" + cause +
                '}';
    }

    public interface Cause { }
}
