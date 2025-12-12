package br.com.leonardo.context.scanner;

import br.com.leonardo.context.resolver.HttpEndpointResolver;
import br.com.leonardo.context.resolver.HttpExceptionHandlerResolver;
import br.com.leonardo.context.resolver.ResolversContextHolder;
import org.reflections.Reflections;

public class Scanners {

    private Scanners(){}

    public static ResolversContextHolder scan(Class<?> clazz) {
        final String pack = clazz.getPackage().getName();
        final Reflections reflections = new Reflections(pack);

        Scanner<HttpEndpointResolver> endpointScanner = new EndpointScanner();
        Scanner<HttpExceptionHandlerResolver> exceptionScanner = new ExceptionHandlerScanner();

        final HttpEndpointResolver endpoints = endpointScanner.scan(reflections);
        final HttpExceptionHandlerResolver exceptions = exceptionScanner.scan(reflections);

        return new ResolversContextHolder(endpoints, exceptions);
    }

}
