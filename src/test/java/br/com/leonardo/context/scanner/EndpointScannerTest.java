package br.com.leonardo.context.scanner;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

@ExtendWith(MockitoExtension.class)
class EndpointScannerTest {

    private final EndpointScanner scanner = new EndpointScanner();

    @Mock
    private Reflections reflections;

    @Test
    void shouldScanEndpoint() {
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> scanner.scan(reflections));
    }
}