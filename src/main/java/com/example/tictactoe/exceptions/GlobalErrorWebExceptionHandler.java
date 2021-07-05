package com.example.tictactoe.exceptions;

import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * This class implements a global exception handler that replaces the default one.
 * <p>
 * Order(-2) annotation specifies a priority. Lower values have bigger priority. We need to specify it to take over the
 * default handler.
 */
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof ServerWebInputException)
            return Mono.error(new DisplayStackTraceInMessageException(ex));
        if (ex.getCause() instanceof ReadTimeoutException)
            return Mono.error(new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                "Unable to create contract due to timeout while connecting to downstream systems, please try again.",
                ex));
        return Mono.error(ex);
    }
}
