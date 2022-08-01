package org.starlingbank.test.proxies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorDetails(String description, @NotBlank String message) {
}
