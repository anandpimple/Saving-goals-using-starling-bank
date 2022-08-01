package org.starlingbank.test.proxies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.starlingbank.test.validators.currrency.CurrencyCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record SavingGoalRequest(String description, @Size(min = 1, max = 255) String name,
                                @CurrencyCode String currency,
                                @NotNull Amount target, String base64EncodedPhoto) {
}
