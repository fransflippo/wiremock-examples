import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

class UserServiceKt(
        @Value("\${userservice.url}") private val baseUrl: String,
        private val restTemplate: RestTemplate
) {

    fun getUsers(): List<UserKt> {
        val responseEntity = restTemplate.exchange(
                "$baseUrl/users",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<UserKt>>() { })
        return responseEntity.body!!
    }

    fun getUsersByName(username: String): List<UserKt> {
        val responseEntity = restTemplate.exchange(
                "$baseUrl/users?name={name}",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<UserKt>>() { },
                username)
        return responseEntity.body!!
    }

    fun createUser(username: String): String {
        val responseEntity = restTemplate.exchange(
                "$baseUrl/users",
                HttpMethod.POST,
                HttpEntity(UserKt(username)),
                Void::class.java)
        val location = responseEntity.headers.location
        val p = location!!.path.lastIndexOf('/')
        return location.path.substring(p + 1)
    }

}