package pl.mbassara.jnapi.web.exception;

public class JNapiException extends RuntimeException {

    public JNapiException(String message, Object... args) {
        super(String.format(message, args));
    }

    public JNapiException(Exception e, String message, Object... args) {
        super(String.format(message, args) + ". " + e.getMessage(), e);
    }
}
