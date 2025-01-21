package com.alterdekim.frida.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ClientConfig {
    @NotNull
    private String private_key;
    @NotNull
    private String public_key;
    @NotNull
    private String address;

    @JsonCreator
    public ClientConfig(@JsonProperty(required = true, value = "private_key") @NotNull String private_key,
                        @JsonProperty(required = true, value = "public_key") @NotNull String public_key,
                        @JsonProperty(required = true, value = "address") @NotNull String address) {
        this.private_key = private_key;
        this.public_key = public_key;
        this.address = address;
    }
}
