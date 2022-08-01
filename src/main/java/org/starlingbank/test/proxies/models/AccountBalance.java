package org.starlingbank.test.proxies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountBalance(Amount totalEffectiveBalance) {
}
