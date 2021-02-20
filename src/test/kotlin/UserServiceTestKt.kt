import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.web.client.RestTemplateBuilder

class UserServiceTestKt {
    private lateinit var userService: UserServiceKt

    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    fun setup() {
        wireMockServer = WireMockServer(WireMockConfiguration
                .wireMockConfig()
                .dynamicPort())
        wireMockServer.start()

        userService = UserServiceKt(wireMockServer.baseUrl(), RestTemplateBuilder().build())
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun testGetUsers() {
        // Given
        wireMockServer.stubFor(
                get(urlEqualTo("/users"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                        [
                                          { "username": "john" },
                                          { "username": "mary" }
                                        ]""".trimIndent())
                        )
        )

        // When
        val users = userService.getUsers()

        // Then
        assertThat(users, contains(
                hasProperty("username", `is`("john")),
                hasProperty("username", `is`("mary"))
        ))
    }

    @Test
    fun testGetUsersByName() {
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
        )

        // When
        val users = userService.getUsersByName("jo&hn")

        // Then
        assertThat(users, contains(
                hasProperty("username", `is`("john"))
        ))
    }

    @Test
    fun testGetUsersByName2() {
        // Given
        wireMockServer.stubFor(
                get(urlPathEqualTo("/users"))
                        .withQueryParam("name", equalTo("john&mary"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                        [
                                          { "username": "john&mary" }
                                        ]""".trimIndent())
                        )
        )

        // When
        val users = userService.getUsersByName("john&mary")

        // Then
        assertThat(users, contains(
                hasProperty("username", `is`("john&mary"))
        ))
    }

    @Test
    fun testCreateUser() {
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
        )

        // When
        val userId = userService.createUser("lucy")

        // Then
        assertEquals("lucy", userId)
    }

}