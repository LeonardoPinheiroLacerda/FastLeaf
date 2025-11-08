package br.com.leonardo.io.output;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.router.context.HttpEndpointContext;
import br.com.leonardo.router.context.HttpEndpoint;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.router.extractor.HeaderExtractor;
import br.com.leonardo.router.extractor.PathVariableExtractor;
import br.com.leonardo.router.extractor.QueryParameterExtractor;
import br.com.leonardo.util.ContentNegotiationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
public record ApiHttpResponseWriter (
        HttpEndpointContext context
) implements HttpWriter {

    @Override
    public HttpResponse<?> generateResponse(RequestLine requestLine, Set<HttpHeader> headers, byte[] body) throws HttpException {

        //TODO: remover essa lógica daqui
        final HttpEndpoint<?, ?> endpointHandler = context.get(requestLine)
                                                          .orElse(null);

        if (endpointHandler == null) {
            log.error("No endpoint handler found for request: {}", requestLine);
            throw new HttpException(
                    "No handler were found for this endpoint " +
                            (
                                    ApplicationProperties.staticContentEnabled()
                                            ? "or no static content were found"
                                            : ""
                            ),
                    HttpStatusCode.NOT_FOUND,
                    requestLine.uri()
            );
        }

        PathVariableMap pathVariableMap     = PathVariableExtractor.extract(requestLine, endpointHandler);
        QueryParameterMap queryParameterMap = QueryParameterExtractor.extract(requestLine);
        HeaderMap headerMap                 = HeaderExtractor.extract(headers);

        endpointHandler
                .runMiddlewares(requestLine, headerMap, body, pathVariableMap, queryParameterMap);

        try {
            return endpointHandler
                    .createResponse(requestLine, headerMap, body, pathVariableMap, queryParameterMap);
        }catch (IOException e) {
            throw new HttpException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR, requestLine.uri());
        }
    }

    @Override
    public byte[] getBody(RequestLine requestLine,
                          Set<HttpHeader> headers,
                          HttpResponse<?> response) throws IOException {
        final HttpHeader acceptHeader = ContentNegotiationUtil.resolveSupportedAcceptHeader(headers);

        byte[] bodyBytes = ContentNegotiationUtil.serializePlainBody(response.getBody(), acceptHeader);
        ContentNegotiationUtil.setContentTypeAndContentLength(acceptHeader, bodyBytes, response);

        return bodyBytes;
    }
}
