package authenticate.gateway.app.exception;

public class ServerException extends RuntimeException {
  public ServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
