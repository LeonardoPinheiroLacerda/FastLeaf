package br.com.leonardo.router.matcher;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EndpointUriMatcherTest {

    private UriMatcher underTest = new EndpointUriMatcher(
            List.of(
                    new PathVariableUriMatcher(),
                    new QueryParameterUriMatcher()
            )
    );

    @Test
    void shouldMatchEndpointUriWithPathVariables() {

        //Given
        final String resolverUri = "/users/{id}";
        final String inputUri = "/users/1";

        //When
        final boolean match = underTest.match(resolverUri, inputUri);

        //Then
        Assertions
                .assertThat(match)
                .isTrue();
    }

    @Test
    void shouldNotMatchEndpointUriWithPathVariables() {

        //Given
        final String resolverUri = "/users/{id}/test";
        final String inputUri = "/users/1";

        //When
        final boolean match = underTest.match(resolverUri, inputUri);

        //Then
        Assertions
                .assertThat(match)
                .isFalse();
    }

    @Test
    void shouldMatchEndpointUriWithQueryParameter() {

        //Given
        final String resolverUri = "/users";
        final String inputUri = "/users?name=test";

        //When
        final boolean match = underTest.match(resolverUri, inputUri);

        //Then
        Assertions
                .assertThat(match)
                .isTrue();
    }

    @Test
    void shouldNotMatchEndpointUriWithQueryParameter() {

        //Given
        final String resolverUri = "/users";
        final String inputUri = "/users/test?name=test";

        //When
        final boolean match = underTest.match(resolverUri, inputUri);

        //Then
        Assertions
                .assertThat(match)
                .isFalse();
    }

}