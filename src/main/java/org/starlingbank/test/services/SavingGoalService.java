package org.starlingbank.test.services;

import org.starlingbank.test.models.AccountSavingGoalRequest;
import org.starlingbank.test.models.ExecuteGoalRequest;
import org.starlingbank.test.models.RegisterAccountSavingGoalResponse;

public interface SavingGoalService {
    RegisterAccountSavingGoalResponse manageSavingGoal(final String token,
                                                       final String accountUid,
                                                       final AccountSavingGoalRequest accountSavingGoalRequest);

    void executeRoundingUpGoalFor(final String token,
                                  final String accountUid,
                                  final ExecuteGoalRequest executeGoalRequest);

}
