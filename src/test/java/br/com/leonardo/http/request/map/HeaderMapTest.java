package br.com.leonardo.http.request.map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

class HeaderMapTest {

    @Test
    void shouldGetStringValue() {
        HeaderMap headerMap = new HeaderMap(Map.of("Content-Type", "application/json"));
        Assertions.assertThat(headerMap.getString("Content-Type")).isEqualTo(Optional.of("application/json"));
    }

    @Test
    void shouldReturnEmptyOptionalForMissingString() {
        HeaderMap headerMap = new HeaderMap(Map.of());
        Assertions.assertThat(headerMap.getString("Content-Type")).isEmpty();
    }

    @Test
    void shouldGetIntegerValue() {
        HeaderMap headerMap = new HeaderMap(Map.of("Content-Length", "123"));
        Assertions.assertThat(headerMap.getInteger("Content-Length")).isEqualTo(Optional.of(123));
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidInteger() {
        HeaderMap headerMap = new HeaderMap(Map.of("Content-Length", "abc"));
        Assertions.assertThatThrownBy(() -> headerMap.getInteger("Content-Length"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetLongValue() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Request-ID", "1234567890123"));
        Assertions.assertThat(headerMap.getLong("X-Request-ID")).isEqualTo(Optional.of(1234567890123L));
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidLong() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Request-ID", "abc"));
        Assertions.assertThatThrownBy(() -> headerMap.getLong("X-Request-ID"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetBooleanValueTrue() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "true"));
        Assertions.assertThat(headerMap.getBoolean("X-Custom-Flag")).isEqualTo(Optional.of(true));
    }

    @Test
    void shouldGetBooleanValueFalse() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "false"));
        Assertions.assertThat(headerMap.getBoolean("X-Custom-Flag")).isEqualTo(Optional.of(false));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidBoolean() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "not-a-boolean"));
        Assertions.assertThatThrownBy(() -> headerMap.getBoolean("X-Custom-Flag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Header 'X-Custom-Flag' is not a valid boolean. Received: not-a-boolean");
    }

    @Test
    void shouldReturnTrueWhenHeaderExists() {
        HeaderMap headerMap = new HeaderMap(Map.of("Accept", "application/json"));
        Assertions.assertThat(headerMap.exists("Accept")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenHeaderDoesNotExist() {
        HeaderMap headerMap = new HeaderMap(Map.of());
        Assertions.assertThat(headerMap.exists("Accept")).isFalse();
    }
}
