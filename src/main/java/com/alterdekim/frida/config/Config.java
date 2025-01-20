package com.alterdekim.frida.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class Config {
    @NotNull
    @JsonProperty(required = true)
    private ClientConfig client;
    @NotNull
    @JsonProperty(required = true)
    private ServerConfig server;

    public Config(@JsonProperty(required = true, value = "client") @NotNull ClientConfig client, @JsonProperty(required = true, value = "server") @NotNull ServerConfig server) {
        this.client = client;
        this.server = server;
    }
}
