package org.starlingbank.test.models;

import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
public record AccountSavingGoalRequest(@Size(min = 1, max = 255) String name, @NotNull @Min(1) long targetAmount) {
}
