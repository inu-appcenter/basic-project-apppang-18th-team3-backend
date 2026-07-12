package shop.apppang.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.apppang.global.exception.ErrorResponse;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(InvalidNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidName(InvalidNameException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrentPassword(InvalidCurrentPasswordException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }
}
