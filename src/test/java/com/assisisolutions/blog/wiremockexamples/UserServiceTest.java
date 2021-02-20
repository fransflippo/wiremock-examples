package com.assisisolutions.blog.wiremockexamples;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    private UserService userService;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration
                .wireMockConfig()
                .dynamicPort());
        wireMockServer.start();

        userService = new UserService(wireMockServer.baseUrl(), new RestTemplateBuilder().build());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testGetUsers() {
        // Given
        wireMockServer.stubFor(
                get(urlEqualTo("/users"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("[\n" +
                                                "  { \"username\": \"john\" },\n" +
                                                "  { \"username\": \"mary\" }\n" +
                                                "]")
                        )
        );

        // When
        List<User> users = userService.getUsers();

        // Then
        assertThat(users, contains(
                hasProperty("username", is("john")),
                hasProperty("username", is("mary"))
        ));
    }

    @Test
    public void testGetUsersByName() {
        // Given
        wireMockServer.stubFor(
                get(urlEqualTo("/users?name=jo%26hn"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("[\n" +
                                                "  { \"username\": \"john\" }\n" +
                                                "]")
                        )
        );

        // When
        List<User> users = userService.getUsersByName("jo&hn");

        // Then
        assertThat(users, contains(
                hasProperty("username", is("john"))
        ));
    }

    @Test
    public void testGetUsersByName2() {
        // Given
        wireMockServer.stubFor(
                get(urlPathEqualTo("/users"))
                        .withQueryParam("name", equalTo("jo&hn"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("[\n" +
                                                "  { \"username\": \"john\" }\n" +
                                                "]")
                        )
        );

        // When
        List<User> users = userService.getUsersByName("jo&hn");

        // Then
        assertThat(users, contains(
                hasProperty("username", is("john"))
        ));
    }

    @Test
    public void testCreateUser() {
        // Given
        wireMockServer.stubFor(
                post(urlEqualTo("/users"))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(
                                equalToJson("{ \"username\": \"lucy\" }")
                        )
                        .willReturn(
                                aResponse()
                                        .withStatus(201)
                                        .withHeader("Location", wireMockServer.baseUrl() + "/users/lucy")
                        )
        );

        // When
        String userId = userService.createUser("lucy");

        // Then
        assertEquals("lucy", userId);
    }

}
