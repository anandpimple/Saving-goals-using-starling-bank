package org.starlingbank.test.models;

import lombok.Builder;

@Builder
public record RegisterAccountSavingGoalResponse(String savingGoalUuid, String name, String accountUuid,
                                                String currency) {
}
