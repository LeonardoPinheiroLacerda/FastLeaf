package br.com.leonardo.exception.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.ToIntFunction;

public class HttpExceptionHandlerResolver {

    private final Map<Class<? extends Throwable>, HttpExceptionHandler<?, ?>> exceptionMap = new ConcurrentSkipListMap<>(
            (o1, o2) -> {
                final ToIntFunction<Class<? extends Throwable>> classDepthFunction = clazz -> {
                    int depth = 0;
                    Class<?> current = clazz;

                    while (current != null) {
                        depth += 1;
                        current = current.getSuperclass();
                    }

                    return depth;
                };

                final Integer depth1 = classDepthFunction.applyAsInt(o1);
                final Integer depth2 = classDepthFunction.applyAsInt(o2);

                final int depthCompare = depth1.compareTo(depth2);

                if (depthCompare == 0) {
                    return o1.getName().compareTo(o2.getName());
                }

                return depthCompare;
            }
    );


    public void add(HttpExceptionHandler<?, ?> httpExceptionHandler) {
        exceptionMap.put(
                httpExceptionHandler.resolveThrowbleType(),
                httpExceptionHandler
        );
    }

    public Optional<HttpExceptionHandler<?, ?>> get(Class<? extends Throwable> exceptionClass) {
        return Optional.ofNullable(exceptionMap.get(exceptionClass));
    }

}
