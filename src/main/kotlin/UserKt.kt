import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class UserKt @JsonCreator constructor(
        @JsonProperty("username")
        val username: String
)