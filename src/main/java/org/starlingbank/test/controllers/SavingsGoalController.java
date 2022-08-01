package org.starlingbank.test.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.starlingbank.test.models.AccountSavingGoalRequest;
import org.starlingbank.test.models.ExecuteGoalRequest;
import org.starlingbank.test.models.RegisterAccountSavingGoalResponse;
import org.starlingbank.test.services.SavingGoalService;
import org.starlingbank.test.validators.uuid.Uuid;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
public class SavingsGoalController {
    private final SavingGoalService savingsGoalService;

    @PostMapping(value = "/account/{accountUuid}/saving-goal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RegisterAccountSavingGoalResponse addSavingGoalEndpoint(final @RequestHeader(name = "Authorization") char[] authHeader,
                                                                   final @PathVariable("accountUuid") @Uuid String accountUuid,
                                                                   final @RequestBody @NotNull @Valid AccountSavingGoalRequest accountSavingGoalRequest) {

        log.info("Performing saving goal manage action on account {}. Goal name is {}", accountUuid, accountSavingGoalRequest.name());
        return savingsGoalService.manageSavingGoal(new String(authHeader), accountUuid, accountSavingGoalRequest);
    }

    @PutMapping(value = "/account/{accountUuid}/execute-round-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void executeRoundUpForAccount(final @RequestHeader(name = "Authorization") char[] authHeader,
                                         final @PathVariable("accountUuid") String accountUuid,
                                         final @RequestBody @NotNull @Valid ExecuteGoalRequest goalRequest) {

        log.info("Executing round up goal for account {} with start date {} and {} previous days", accountUuid, goalRequest.fromDate(), goalRequest.previousDays());
        savingsGoalService.executeRoundingUpGoalFor(new String(authHeader), accountUuid, goalRequest);
    }
}
