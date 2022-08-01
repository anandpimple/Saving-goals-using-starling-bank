package org.starlingbank.test.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.starlingbank.test.entities.AccountSavingGoal;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountSavingGoalRepo extends CrudRepository<AccountSavingGoal, String> {
    List<AccountSavingGoal> findAllByAccountUuid(String accountUuid);

    Optional<AccountSavingGoal> findByAccountUuidAndGoalName(String accountUuid, String goalName);
}
