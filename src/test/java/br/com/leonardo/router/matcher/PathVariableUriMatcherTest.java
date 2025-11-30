package br.com.leonardo.router.matcher;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathVariableUriMatcherTest {

    private final PathVariableUriMatcher matcher = new PathVariableUriMatcher();

    @Test
    void shouldMatch_whenUrisAreIdentical() {
        // Given
        String resolverUri = "/users/123";
        String inputUri = "/users/123";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldMatch_whenPathVariableIsPresent() {
        // Given
        String resolverUri = "/users/{id}";
        String inputUri = "/users/456";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotMatch_whenUrisHaveDifferentLengths() {
        // Given
        String resolverUri = "/users/{id}";
        String inputUri = "/users/123/orders"; // Input has more segments

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();

        // Given
        resolverUri = "/users/123/orders"; // Resolver has more segments
        inputUri = "/users/{id}";

        // When
        result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotMatch_whenSegmentsDoNotMatchAndNotPathVariable() {
        // Given
        String resolverUri = "/users/admin";
        String inputUri = "/users/guest";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldMatchWithMultiplePathVariables() {
        // Given
        String resolverUri = "/users/{userId}/orders/{orderId}";
        String inputUri = "/users/789/orders/abc";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotMatchWithPartialMismatch() {
        // Given
        String resolverUri = "/users/{userId}/profile";
        String inputUri = "/users/123/settings"; // 'profile' vs 'settings'

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldHandleRootPath() {
        // Given
        String resolverUri = "/";
        String inputUri = "/";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldHandleRootPathWithPathVariable() {
        // Given
        String resolverUri = "/{id}";
        String inputUri = "/test";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotMatchRootPathWithExtraSegment() {
        // Given
        String resolverUri = "/";
        String inputUri = "/test";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotMatchExtraSegmentWithRootPath() {
        // Given
        String resolverUri = "/test";
        String inputUri = "/";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldMatchComplexPathWithMixedSegments() {
        // Given
        String resolverUri = "/api/v1/{resource}/item/{itemId}";
        String inputUri = "/api/v1/products/item/P001";

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotMatchIfNonPathVariableSegmentDiffers() {
        // Given
        String resolverUri = "/api/v1/users/{id}";
        String inputUri = "/api/v2/users/123"; // 'v1' vs 'v2'

        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }
}
