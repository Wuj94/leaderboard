package com.example.tictactoe.exceptions;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DisplayStackTraceInMessageException extends ResponseStatusException {

    private static final long serialVersionUID = -5824395818170863058L;
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public DisplayStackTraceInMessageException(Throwable cause) {
        super(HTTP_STATUS, convertStackTraceIntoExceptionMessage(cause), cause);
    }

    private static String convertStackTraceIntoExceptionMessage(Throwable e) {
        return getNestedExceptions(e)
            .map(Throwable::getMessage)
            .collect(Collectors.joining("\n")).replaceAll("\"", "'");
    }

    private static Stream<Throwable> getNestedExceptions(Throwable t) {
        return t == null ? Stream.empty() : Stream.concat(Stream.of(t), getNestedExceptions(t.getCause()));
    }
}
