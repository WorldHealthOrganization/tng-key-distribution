package tng.trustnetwork.keydistribution.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class TrustListExceptionHandler {
    @ExceptionHandler(TrustListNotFoundException.class)
    public ResponseEntity<?> trustListNotFoundException(TrustListNotFoundException ex, WebRequest request) {
        TrustListException trustListException = new TrustListException(
            new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(trustListException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> trustListExcpetionHandler(Exception ex, WebRequest request) {
        TrustListException errorDetails = new TrustListException(
            new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}