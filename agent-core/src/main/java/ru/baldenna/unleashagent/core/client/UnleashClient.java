package ru.baldenna.unleashagent.core.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import ru.baldenna.unleashagent.core.common.auth.LoginRequest;
import ru.baldenna.unleashagent.core.common.auth.UserDTO;
import ru.baldenna.unleashagent.core.common.config.FeignConfig;
import ru.baldenna.unleashagent.core.features.model.CreateFeatureDto;
import ru.baldenna.unleashagent.core.features.model.FeaturesResponse;
import ru.baldenna.unleashagent.core.features.model.UpdateFeatureDto;
import ru.baldenna.unleashagent.core.tags.model.Tag;
import ru.baldenna.unleashagent.core.tags.model.TagListResponse;

public interface UnleashClient {

    @RequestLine("POST /auth/simple/login")
    @Headers("Content-Type: application/json")
    Response login(LoginRequest loginRequest);

    @RequestLine("POST /api/admin/projects/{projectId}/features")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    String createFeature(
            @Param("projectId") String projectId,
            CreateFeatureDto createFeatureDto,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("GET /api/admin/search/features?limit={limit}&project={project}")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    FeaturesResponse getFeatures(
            @Param("limit") int limit,
            @Param("project") String project,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/projects/{projectId}/features/{featureName}")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void archiveFeature(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("PUT /api/admin/projects/{projectId}/features/{featureName}")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void updateFeature(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            UpdateFeatureDto dto,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("GET /api/admin/tags")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    TagListResponse getTags(
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/tags/{type}/{value}")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void deleteTag(
            @Param("type") String type,
            @Param("value") String value,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("POST /api/admin/tags")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void createTag(
            Tag tagRequest,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("GET /api/admin/tag-types")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    String getTagTypes(
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("POST /api/admin/features/{featureName}/tags")
    @Headers({
        "Content-Type: application/json",
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void addTagToFeature(
            @Param("featureName") String featureName,
            Tag tagRequest,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/features/{featureName}/tags/{type}/{value}")
    @Headers({
        "Accept: application/json",
        "Cookie: unleash-session={sessionCookie}"
    })
    void deleteTagFromFeature(
            @Param("featureName") String featureName,
            @Param("type") String type,
            @Param("value") String value,
            @Param("sessionCookie") String sessionCookie
    );

}
