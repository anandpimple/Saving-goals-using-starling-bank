package org.starlingbank.test.advices;

import feign.FeignException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionMapperAdvice {

    @ExceptionHandler({ConstraintViolationException.class, FeignException.BadRequest.class})
    @SneakyThrows
    public void badRequestExceptionHandler(Exception exception,
                                           ServletWebRequest webRequest) {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
