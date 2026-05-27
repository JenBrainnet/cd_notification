package ru.checkdev.notification.telegram.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TgAuthCallWebClientRetryTest {

    @Test
    void whenDoGetThenReturnProfile() {
        var profile = new Profile();
        profile.setId(1);
        profile.setUsername("User");
        var webClient = mock(WebClient.class);
        var uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var headersSpec = mock(WebClient.RequestHeadersSpec.class);
        var responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri("/profiles/tg/1")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Profile.class)).thenReturn(Mono.just(profile));
        var client = new TgAuthCallWebClient(webClient);

        Profile result = client.doGet("/profiles/tg/1").block();

        assertThat(result).isEqualTo(profile);
    }

    @Test
    void whenDoPostThenReturnObject() {
        var profile = new Profile();
        profile.setEmail("user@mail.ru");
        var webClient = mock(WebClient.class);
        var uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        var bodySpec = mock(WebClient.RequestBodySpec.class);
        var headersSpec = mock(WebClient.RequestHeadersSpec.class);
        var responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/profiles/tg/byEmailAndPassword")).thenReturn(bodySpec);
        when(bodySpec.bodyValue(profile)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(profile));
        var client = new TgAuthCallWebClient(webClient);

        Object result = client.doPost("/profiles/tg/byEmailAndPassword", profile).block();

        assertThat(result).isEqualTo(profile);
    }

    @Test
    void whenGetFallbackThenReturnEmptyMono() {
        var client = new TgAuthCallWebClient(mock(WebClient.class));

        Profile result = client.fallbackGet("/profiles/tg/1",
                new IllegalStateException("auth unavailable")).block();

        assertThat(result).isNull();
    }

    @Test
    void whenPostFallbackThenReturnEmptyMono() {
        var client = new TgAuthCallWebClient(mock(WebClient.class));

        Object result = client.fallbackPost("/profiles/tg/byEmailAndPassword",
                new Profile(), new IllegalStateException("auth unavailable")).block();

        assertThat(result).isNull();
    }

    @Test
    void whenDoGetThenHasRetryAndCircuitBreakerAnnotations() throws NoSuchMethodException {
        var method = TgAuthCallWebClient.class.getMethod("doGet", String.class);

        assertThat(method.getAnnotation(Retry.class).name()).isEqualTo("tgAuthRetry");
        assertThat(method.getAnnotation(CircuitBreaker.class).name()).isEqualTo("tgAuthCircuitBreaker");
        assertThat(method.getAnnotation(CircuitBreaker.class).fallbackMethod()).isEqualTo("fallbackGet");
    }

    @Test
    void whenDoPostThenHasRetryAndCircuitBreakerAnnotations() throws NoSuchMethodException {
        var method = TgAuthCallWebClient.class.getMethod("doPost", String.class, Profile.class);

        assertThat(method.getAnnotation(Retry.class).name()).isEqualTo("tgAuthRetry");
        assertThat(method.getAnnotation(CircuitBreaker.class).name()).isEqualTo("tgAuthCircuitBreaker");
        assertThat(method.getAnnotation(CircuitBreaker.class).fallbackMethod()).isEqualTo("fallbackPost");
    }
}
