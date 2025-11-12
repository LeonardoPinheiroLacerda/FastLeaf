package br.com.leonardo.router.extractor;

import br.com.leonardo.enums.ContentType;
import br.com.leonardo.enums.HttpHeader;
import br.com.leonardo.http.request.map.HeaderMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

class HeaderExtractorTest {


    @Test
    void shouldExtractHeaders() {
        //Given
        Set<br.com.leonardo.http.HttpHeader> headers = Sets.set(
                new br.com.leonardo.http.HttpHeader(HttpHeader.CONTENT_TYPE.getName(), ContentType.APPLICATION_JSON.getType()),
                new br.com.leonardo.http.HttpHeader(HttpHeader.ACCEPT.getName(), ContentType.APPLICATION_JSON.getType())
        );

        //When
        final HeaderMap extract = HeaderExtractor.extract(headers);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(
                        h -> h.getString(HttpHeader.CONTENT_TYPE.getName()).orElse(null),
                        h -> h.getString(HttpHeader.ACCEPT.getName()).orElse(null),
                        h -> h.getString(HttpHeader.AUTHORIZATION.getName()).orElse(null)
                )
                .containsExactly(ContentType.APPLICATION_JSON.getType(), ContentType.APPLICATION_JSON.getType(), null);
    }

}
