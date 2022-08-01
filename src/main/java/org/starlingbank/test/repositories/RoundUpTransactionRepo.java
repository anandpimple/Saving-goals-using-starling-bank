package org.starlingbank.test.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.starlingbank.test.entities.AccountSavingGoal;
import org.starlingbank.test.entities.RoundUpTransaction;

@Repository
public interface RoundUpTransactionRepo extends CrudRepository<RoundUpTransaction, Long> {
    boolean existsByTransactionUuidAndAccountSavingGoal(String transactionUuid, AccountSavingGoal accountSavingGoal);
}
