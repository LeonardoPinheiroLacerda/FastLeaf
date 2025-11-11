package br.com.leonardo.annotation.scanner;

import br.com.leonardo.router.core.HttpEndpointResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EndpointScannerTest {

    private HttpEndpointResolver resolver = new HttpEndpointResolver();

    private EndpointScanner scanner = new EndpointScanner(resolver);

    @Test
    void shouldScanEndpoint() {
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> scanner.scan(this.getClass()));
    }
}