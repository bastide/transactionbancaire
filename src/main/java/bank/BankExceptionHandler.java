package bank;

import java.util.NoSuchElementException;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class BankExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorMessage handleNotFound(NoSuchElementException ex, WebRequest request) {
    return new ErrorMessage("Un des comptes est inconnu " + ex.getMessage());
  }

  @ExceptionHandler(TransactionSystemException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleTransaction(TransactionSystemException ex, WebRequest request) {
    return new ErrorMessage("Crédit insuffisant - " + ex.getMessage());
  }

}