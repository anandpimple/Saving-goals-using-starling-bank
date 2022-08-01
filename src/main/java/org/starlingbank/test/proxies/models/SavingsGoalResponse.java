package org.starlingbank.test.proxies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.starlingbank.test.validators.uuid.Uuid;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SavingsGoalResponse(String description, @Uuid String savingsGoalUid, boolean success,
                                  List<ErrorDetails> errors) {
}
