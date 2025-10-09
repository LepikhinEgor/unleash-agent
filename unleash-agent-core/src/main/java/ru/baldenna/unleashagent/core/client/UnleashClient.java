package ru.baldenna.unleashagent.core.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import ru.baldenna.unleashagent.core.auth.LoginRequest;
import ru.baldenna.unleashagent.core.contextfields.model.ContextField;
import ru.baldenna.unleashagent.core.features.model.CreateFeatureDto;
import ru.baldenna.unleashagent.core.features.model.FeaturesResponse;
import ru.baldenna.unleashagent.core.features.model.UpdateFeatureDto;
import ru.baldenna.unleashagent.core.projects.ProjectsResponse;
import ru.baldenna.unleashagent.core.segments.model.CreateSegmentRequest;
import ru.baldenna.unleashagent.core.segments.model.SegmentListResponse;
import ru.baldenna.unleashagent.core.segments.model.UpdateSegmentRequest;
import ru.baldenna.unleashagent.core.strategies.model.Strategy;
import ru.baldenna.unleashagent.core.tags.model.Tag;
import ru.baldenna.unleashagent.core.tags.model.TagListResponse;
import ru.baldenna.unleashagent.core.tagtypes.TagType;
import ru.baldenna.unleashagent.core.tagtypes.TagTypes;
import ru.baldenna.unleashagent.core.apitokens.model.ApiToken;
import ru.baldenna.unleashagent.core.apitokens.model.ApiTokenListResponse;
import ru.baldenna.unleashagent.core.apitokens.model.CreateApiTokenRequest;
import ru.baldenna.unleashagent.core.apitokens.model.UpdateApiTokenRequest;

import java.util.List;

/**
 * Unleash API client.
 * See <a href="https://docs.getunleash.io/api-overview">Unleash api</a>
 */
public interface UnleashClient {

    int FEATURES_LIMIT = 100000;

    @RequestLine("POST /auth/simple/login")
    @Headers("Content-Type: application/json")
    Response login(LoginRequest loginRequest);

    @RequestLine("POST /api/admin/projects/{projectId}/features")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void createFeature(
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

    default FeaturesResponse getFeatures(String project, String sessionCookie) {
        return getFeatures(FEATURES_LIMIT, "IS:" + project, sessionCookie);
    }

    @RequestLine("GET /api/admin/search/features?limit={limit}&project={project}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    String getFeaturesString(
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

    @RequestLine("DELETE /api/admin/archive/{featureName}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void deleteFeature(
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
    TagTypes getTagTypes(
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("POST /api/admin/tag-types")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void createTagType(
            TagType tagType,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/tag-types/{name}")
    @Headers({
            "Cookie: unleash-session={sessionCookie}",
            "Accept: application/json"
    })
    void deleteTagType(
            @Param("name") String name,
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
            Tag tag,
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

    @RequestLine("GET /api/admin/projects")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    ProjectsResponse getProjects(@Param("sessionCookie") String sessionCookie);

    @RequestLine("POST /api/admin/segments")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void createSegment(CreateSegmentRequest createSegmentRequest, @Param("sessionCookie") String sessionCookie);

    @RequestLine("DELETE /api/admin/segments/{id}")
    @Headers({
            "Cookie: unleash-session={sessionCookie}",
            "Accept: application/json"
    })
    void deleteSegment(@Param("id") Integer segmentId, @Param("sessionCookie") String sessionCookie);

    @RequestLine("PUT /api/admin/segments/{id}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void updateSegment(
            @Param("id") Integer segmentId,
            UpdateSegmentRequest updatedSegment,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("GET /api/admin/segments")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    SegmentListResponse getSegments(@Param("sessionCookie") String sessionCookie);

    @RequestLine("GET /api/admin/context")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    List<ContextField> getContextFields(@Param("sessionCookie") String sessionCookie);

    @RequestLine("POST /api/admin/context")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void createContextField(ContextField contextField, @Param("sessionCookie") String sessionCookie);

    @RequestLine("PUT /api/admin/context/{name}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void updateContextField(
            @Param("name") String name,
            ContextField updatedContextField,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/context/{name}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void deleteContextField(@Param("name") String name, @Param("sessionCookie") String sessionCookie);

    @RequestLine("GET /api/admin/projects/{projectId}/features/{featureName}/environments/{environment}/strategies")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    List<Strategy> getFeatureStrategies(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            @Param("environment") String environment,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("POST /api/admin/projects/{projectId}/features/{featureName}/environments/{environment}/strategies")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void addFeatureStrategy(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            @Param("environment") String environment,
            Strategy strategy,
            @Param("sessionCookie") String sessionCookie
    );


    @RequestLine("DELETE /api/admin/projects/{projectId}/features/{featureName}/environments/{environment}/strategies/{strategyId}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void deleteFeatureStrategy(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            @Param("environment") String environment,
            @Param("strategyId") String strategyId,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("PUT /api/admin/projects/{projectId}/features/{featureName}/environments/{environment}/strategies/{strategyId}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void updateFeatureStrategy(
            @Param("projectId") String projectId,
            @Param("featureName") String featureName,
            @Param("environment") String environment,
            @Param("strategyId") String strategyId,
            Strategy updatedStrategy,
            @Param("sessionCookie") String sessionCookie
    );

    // API tokens
    @RequestLine("GET /api/admin/api-tokens")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    ApiTokenListResponse getApiTokens(@Param("sessionCookie") String sessionCookie);

    @RequestLine("GET /api/admin/api-tokens/{name}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    ApiToken getApiTokenByName(
            @Param("name") String name,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("POST /api/admin/api-tokens")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void createApiToken(
            CreateApiTokenRequest request,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("PUT /api/admin/api-tokens/{secret}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void updateApiToken(
            @Param("secret") String token,
            UpdateApiTokenRequest request,
            @Param("sessionCookie") String sessionCookie
    );

    @RequestLine("DELETE /api/admin/api-tokens/{secret}")
    @Headers({
            "Accept: application/json",
            "Cookie: unleash-session={sessionCookie}"
    })
    void deleteApiToken(
            @Param("secret") String token,
            @Param("sessionCookie") String sessionCookie
    );

}
