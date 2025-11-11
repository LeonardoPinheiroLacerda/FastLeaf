package br.com.leonardo.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ApplicationPropertiesTest {

    @Test
    void shouldGetPort() {
        final int port = ApplicationProperties.getPort();
        Assertions.assertThat(port).isEqualTo(9000);
    }

    @Test
    void shouldGetLogRequests() {
        final boolean logRequests = ApplicationProperties.shouldLogRequests();
        Assertions.assertThat(logRequests).isFalse();
    }
    @Test
    void shouldGetLogResponses() {
        final boolean logResponses = ApplicationProperties.shouldLogResponses();
        Assertions.assertThat(logResponses).isFalse();
    }

    @Test
    void shouldGetStaticFolder() {
        final String staticContentPath = ApplicationProperties.getStaticContentPath();
        Assertions.assertThat(staticContentPath).isEqualTo("static");
    }

    @Test
    void shouldGetStaticContentEnabled() {
        final boolean b = ApplicationProperties.staticContentEnabled();
        Assertions.assertThat(b).isTrue();
    }

}