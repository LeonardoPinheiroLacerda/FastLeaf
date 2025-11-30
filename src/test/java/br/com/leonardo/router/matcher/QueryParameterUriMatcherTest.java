package br.com.leonardo.router.matcher;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterUriMatcherTest {

    private final QueryParameterUriMatcher matcher = new QueryParameterUriMatcher();

    @Test
    void shouldNotMatch_whenTrailingSlashMismatch() {
        // Given
        String resolverUri = "/products";
        String inputUriWithSlash = "/products/";
        String inputUriWithSlashAndParams = "/products/?id=1";
        String resolverUriWithSlash = "/products/";
        String inputUriWithoutSlash = "/products";


        // When & Then
        assertThat(matcher.match(resolverUri, inputUriWithSlash)).isFalse();
        assertThat(matcher.match(resolverUri, inputUriWithSlashAndParams)).isFalse();
        assertThat(matcher.match(resolverUriWithSlash, inputUriWithoutSlash)).isFalse();
    }

}
