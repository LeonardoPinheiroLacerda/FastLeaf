package br.com.leonardo.http.request.map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.NoSuchElementException;

class PathVariableMapTest {

    @Test
    void shouldGetStringValue() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "123"));
        Assertions.assertThat(pathVariableMap.getString("id")).isEqualTo("123");
    }

    @Test
    void shouldThrowNoSuchElementExceptionForMissingString() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of());
        Assertions.assertThatThrownBy(() -> pathVariableMap.getString("id"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Required path variable 'id' is missing.");
    }

    @Test
    void shouldGetIntegerValue() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "456"));
        Assertions.assertThat(pathVariableMap.getInteger("id")).isEqualTo(456);
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidInteger() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "abc"));
        Assertions.assertThatThrownBy(() -> pathVariableMap.getInteger("id"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetLongValue() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "1234567890123"));
        Assertions.assertThat(pathVariableMap.getLong("id")).isEqualTo(1234567890123L);
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidLong() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "abc"));
        Assertions.assertThatThrownBy(() -> pathVariableMap.getLong("id"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetBooleanValueTrue() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("active", "true"));
        Assertions.assertThat(pathVariableMap.getBoolean("active")).isTrue();
    }

    @Test
    void shouldGetBooleanValueFalse() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("active", "false"));
        Assertions.assertThat(pathVariableMap.getBoolean("active")).isFalse();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidBoolean() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("active", "not-a-boolean"));
        Assertions.assertThatThrownBy(() -> pathVariableMap.getBoolean("active"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Path variable 'active' is not a valid boolean. Received: not-a-boolean");
    }

    @Test
    void shouldReturnTrueWhenPathVariableExists() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of("id", "123"));
        Assertions.assertThat(pathVariableMap.exists("id")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenPathVariableDoesNotExist() {
        PathVariableMap pathVariableMap = new PathVariableMap(Map.of());
        Assertions.assertThat(pathVariableMap.exists("id")).isFalse();
    }
}
