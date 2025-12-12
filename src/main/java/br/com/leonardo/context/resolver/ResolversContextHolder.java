package br.com.leonardo.context.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResolversContextHolder {

    private final HttpEndpointResolver httpEndpointResolver;
    private final HttpExceptionHandlerResolver httpExceptionHandlerResolver;

}
