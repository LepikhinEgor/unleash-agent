package ru.alfabank.dfa.unleash.agent;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.alfabank.dfa.unleash.agent.apitokens.model.CreateApiTokenRequest;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class ApiTokenUpdateTest extends AbstractUnleashTest {

    @Test
    public void shouldCreateApiToken_whenApiTokenNotExistsInUnleash() throws IOException {
        // given
        var configuration = parseUnleashConfigFile("ApiTokenConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var tokens = unleashClient.getApiTokens(sessionManager.getSessionCookie()).tokens();

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0).tokenName()).isEqualTo("test-api-token");
        assertThat(tokens.get(0).secret()).isNotNull();
        assertThat(tokens.get(0).type()).isEqualTo("client"); // unleash always rewrite backend to client
        assertThat(tokens.get(0).environment()).isEqualTo("development");
        assertThat(tokens.get(0).project()).isEqualTo("default");
        assertThat(tokens.get(0).projects()).isEqualTo(List.of("default"));
    }

    @Test
    public void shouldDoNothing_whenSynchronizationCalledSecondTime() {
        // given
        var configuration = parseUnleashConfigFile("ApiTokenConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        Mockito.verify(unleashClient, Mockito.times(1)).createApiToken(any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).updateApiToken(any(), any(), any());
        Mockito.verify(unleashClient, Mockito.times(0)).deleteApiToken(any(), any());
    }

    @Test
    public void shouldUpdateApiToken_whenExpiresAtChanged() throws IOException {
        // given
        unleashClient.createApiToken(new CreateApiTokenRequest(
                "2099-01-01T00:00:00+00:00", "client", "development",
                "default", List.of("default"), "test-api-token"
        ), sessionManager.getSessionCookie());
        var tokenSecret = unleashClient.getApiTokens(sessionManager.getSessionCookie()).tokens()
                .stream()
                .filter(t -> "test-api-token".equals(t.tokenName())).findFirst().get().secret();

        var configuration = parseUnleashConfigFile("ApiTokenConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var token = unleashClient.getApiTokens(sessionManager.getSessionCookie()).tokens()
                .stream()
                .filter(t -> "test-api-token".equals(t.tokenName())).findFirst()
                .orElseThrow();
        assertThat(ZonedDateTime.parse(token.expiresAt())).isEqualTo(ZonedDateTime.parse("2025-01-01T00:00:00+00:00"));
        assertThat(token.secret()).isEqualTo(tokenSecret); // token updated, not recreated
    }

    @Test
    public void shouldDeleteApiToken_whenNotFoundInConfiguration() throws IOException {
        // given
        unleashClient.createApiToken(new CreateApiTokenRequest(
                "2099-01-01T00:00:00+00:00", "client", "development",
                "default", List.of("default"), "test-api-token-old"
        ), sessionManager.getSessionCookie());

        var configuration = parseUnleashConfigFile("ApiTokenConfig.yaml");

        // when
        unleashAgent.synchronizeConfiguration(configuration);

        // then
        var tokens = unleashClient.getApiTokens(sessionManager.getSessionCookie()).tokens();
        assertThat(tokens).noneMatch(t -> "test-api-token-old".equals(t.tokenName()));
        assertThat(tokens).hasSize(1);
    }
}


