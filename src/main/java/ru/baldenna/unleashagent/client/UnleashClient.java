package ru.baldenna.unleashagent.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.baldenna.unleashagent.common.config.FeignConfig;
import ru.baldenna.unleashagent.features.CreateFeatureDto;
import ru.baldenna.unleashagent.features.FeaturesResponse;
import ru.baldenna.unleashagent.common.auth.LoginRequest;
import ru.baldenna.unleashagent.tags.Tag;
import ru.baldenna.unleashagent.tags.TagListResponse;
import ru.baldenna.unleashagent.features.UpdateFeatureDto;
import ru.baldenna.unleashagent.common.auth.UserDTO;

@FeignClient(name = "unleash-client", url = "http://unleash.cbclusterint.alfaintra.net/", configuration = FeignConfig.class)
public interface UnleashClient {

    @PostMapping(value = "/auth/simple/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest);

    @PostMapping(value = "api/admin/projects/{projectId}/features", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String createFeature(
            @PathVariable("projectId") String projectId,
            @RequestBody CreateFeatureDto createFeatureDto,
            @CookieValue("unleash-session") String sessionCookie
    );


    @GetMapping(value = "/api/admin/search/features", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FeaturesResponse> getFeatures(
            @RequestParam("limit") int limit,
            @RequestParam("project") String project,
            @CookieValue("unleash-session") String sessionCookie
    );


    @DeleteMapping(value = "/api/admin/projects/{projectId}/features/{featureName}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> archiveFeature(
            @PathVariable("projectId") String projectId,
            @PathVariable("featureName") String featureName,
            @CookieValue("unleash-session") String sessionCookie
    );

    @PutMapping(value = "/api/admin/projects/{projectId}/features/{featureName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> updateFeature(
            @PathVariable("projectId") String projectId,
            @PathVariable("featureName") String featureName,
            @RequestBody UpdateFeatureDto dto,
            @CookieValue("unleash-session") String sessionCookie
    );

    @GetMapping(value = "/api/admin/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TagListResponse> getTags(
            @CookieValue("unleash-session") String sessionCookie
    );

    @DeleteMapping(value = "/api/admin/tags/{type}/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteTag(
            @PathVariable("value") String value,
            @PathVariable("type") String type,
            @CookieValue("unleash-session") String sessionCookie
    );

    @PostMapping(value = "/api/admin/tags", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> createTag(
            @RequestBody Tag tagRequest,
            @CookieValue("unleash-session") String sessionCookie
    );

    @GetMapping(value = "/api/admin/tag-types", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getTagTypes(
            @CookieValue("unleash-session") String sessionCookie
    );


}
