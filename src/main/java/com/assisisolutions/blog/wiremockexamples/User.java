package com.assisisolutions.blog.wiremockexamples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

    private final String username;

    @JsonCreator
    public User(@JsonProperty("username") String username) {
        this.username = username;
    }
}
