package io.vertx.mvc.exceptions;

public class HttpException extends Throwable {
    private static final long serialVersionUID = -6908483402149562812L;

    private int statusCode;
    private String statusMessage;

    public HttpException(int statusCode, String statusMessage) {
        super(statusMessage);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
