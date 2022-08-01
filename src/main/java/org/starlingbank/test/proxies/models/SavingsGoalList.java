package org.starlingbank.test.proxies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SavingsGoalList(String description, List<SavingsGoal> savingsGoalList) {
}
