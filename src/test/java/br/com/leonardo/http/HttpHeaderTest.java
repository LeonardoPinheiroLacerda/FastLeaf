package br.com.leonardo.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    void shouldCreateHttpHeaderAndGetValue() {
        // Given
        String name = "Content-Type";
        String value = "application/json";

        // When
        HttpHeader header = new HttpHeader(name, value);

        // Then
        Assertions.assertThat(header).isNotNull();
        Assertions.assertThat(header.name()).isEqualTo(name);
        Assertions.assertThat(header.value()).isEqualTo(value);
    }
}
