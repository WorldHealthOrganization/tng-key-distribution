package tng.trustnetwork.keydistribution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TrustListNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TrustListNotFoundException(String message) {
        super(message);
    }

    public TrustListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
