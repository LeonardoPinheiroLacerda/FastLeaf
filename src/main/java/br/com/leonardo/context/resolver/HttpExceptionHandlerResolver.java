package br.com.leonardo.context.resolver;

import br.com.leonardo.exception.handler.HttpExceptionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpExceptionHandlerResolver implements Resolver<HttpExceptionHandler<?, ?>, Class<? extends Throwable>> {

    private final Map<Class<? extends Throwable>, HttpExceptionHandler<?, ?>> exceptionMap = new ConcurrentHashMap<>();

    public void add(HttpExceptionHandler<?, ?> httpExceptionHandler) {
        exceptionMap.put(
                httpExceptionHandler.resolveThrowbleType(),
                httpExceptionHandler
        );
    }

    public Optional<HttpExceptionHandler<?, ?>> get(Class<? extends Throwable> exceptionClass) {
        return Optional.ofNullable(exceptionMap.get(exceptionClass));
    }

    @SuppressWarnings("unchecked")
    public Optional<HttpExceptionHandler<?, ?>> getRecursive(Class<? extends Throwable> exceptionClass) {
        final Optional<HttpExceptionHandler<?, ?>> httpExceptionHandler = get(exceptionClass);

        if(httpExceptionHandler.isEmpty() && exceptionClass.getSuperclass() != Throwable.class) {
            return getRecursive((Class<? extends Throwable>) exceptionClass.getSuperclass());
        }

        return httpExceptionHandler;
    }

}
