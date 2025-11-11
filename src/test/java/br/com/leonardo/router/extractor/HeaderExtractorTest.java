package br.com.leonardo.router.extractor;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.request.map.HeaderMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

class HeaderExtractorTest {


    @Test
    void shouldExtractHeaders() {
        //Given
        Set<HttpHeader> headers = Sets.set(
                new HttpHeader("Content-Type", "application/json"),
                new HttpHeader("Accept", "application/json")
        );

        //When
        final HeaderMap extract = HeaderExtractor.extract(headers);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(
                        h -> h.getString("Content-Type").orElse(null),
                        h -> h.getString("Accept").orElse(null),
                        h -> h.getString("Authorization").orElse(null)
                )
                .containsExactly("application/json", "application/json", null);
    }

}