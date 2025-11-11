package br.com.leonardo.http.request.map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

class QueryParameterMapTest {

    @Test
    void shouldGetStringValue() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("name", "leo"));
        Assertions.assertThat(queryParameterMap.getString("name")).isEqualTo(Optional.of("leo"));
    }

    @Test
    void shouldReturnEmptyOptionalForMissingString() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of());
        Assertions.assertThat(queryParameterMap.getString("name")).isEmpty();
    }

    @Test
    void shouldGetIntegerValue() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("age", "30"));
        Assertions.assertThat(queryParameterMap.getInteger("age")).isEqualTo(Optional.of(30));
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidInteger() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("age", "abc"));
        Assertions.assertThatThrownBy(() -> queryParameterMap.getInteger("age"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetLongValue() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("id", "9876543210"));
        Assertions.assertThat(queryParameterMap.getLong("id")).isEqualTo(Optional.of(9876543210L));
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidLong() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("id", "abc"));
        Assertions.assertThatThrownBy(() -> queryParameterMap.getLong("id"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetBooleanValueTrue() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("active", "true"));
        Assertions.assertThat(queryParameterMap.getBoolean("active")).isEqualTo(Optional.of(true));
    }

    @Test
    void shouldGetBooleanValueFalse() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("active", "false"));
        Assertions.assertThat(queryParameterMap.getBoolean("active")).isEqualTo(Optional.of(false));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidBoolean() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("active", "not-a-boolean"));
        Assertions.assertThatThrownBy(() -> queryParameterMap.getBoolean("active"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query parameter 'active' is not a valid boolean. Received: not-a-boolean");
    }

    @Test
    void shouldReturnTrueWhenQueryParamExists() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of("search", "query"));
        Assertions.assertThat(queryParameterMap.exists("search")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenQueryParamDoesNotExist() {
        QueryParameterMap queryParameterMap = new QueryParameterMap(Map.of());
        Assertions.assertThat(queryParameterMap.exists("search")).isFalse();
    }
}
