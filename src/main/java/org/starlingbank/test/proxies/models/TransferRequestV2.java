package org.starlingbank.test.proxies.models;

import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
public record TransferRequestV2(String description, @NotNull Amount amount) {
}
