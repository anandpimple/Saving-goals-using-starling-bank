package org.starlingbank.test.proxies.models;

import lombok.Builder;
import org.starlingbank.test.validators.currrency.CurrencyCode;

import javax.validation.constraints.Min;

@Builder
public record Amount(@CurrencyCode String currency, @Min(0) long minorUnits) {
}
