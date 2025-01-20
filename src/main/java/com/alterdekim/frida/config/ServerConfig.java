package com.alterdekim.frida.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ServerConfig {
    @NotNull
    private String public_key;
    @NotNull
    private String endpoint;
    @NotNull
    private String internal_gateway;
    private byte keepalive;

    @JsonCreator
    public ServerConfig(@JsonProperty(required = true, value = "public_key") @NotNull String public_key,
                        @JsonProperty(required = true, value = "endpoint") @NotNull String endpoint,
                        @JsonProperty(required = true, value = "internal_gateway") @NotNull String internal_gateway,
                        @JsonProperty(required = true, value = "keepalive") byte keepalive) {
        this.public_key = public_key;
        this.endpoint = endpoint;
        this.internal_gateway = internal_gateway;
        this.keepalive = keepalive;
    }
}