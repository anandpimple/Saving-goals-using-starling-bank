package org.starlingbank.test.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.starlingbank.test.entities.AccountSavingGoal;
import org.starlingbank.test.entities.RoundUpTransaction;
import org.starlingbank.test.models.AccountSavingGoalRequest;
import org.starlingbank.test.models.ExecuteGoalRequest;
import org.starlingbank.test.models.RegisterAccountSavingGoalResponse;
import org.starlingbank.test.proxies.StarlingProxy;
import org.starlingbank.test.proxies.models.*;
import org.starlingbank.test.repositories.AccountSavingGoalRepo;
import org.starlingbank.test.repositories.RoundUpTransactionRepo;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.starlingbank.test.proxies.models.TransactionDirection.OUT;
import static org.starlingbank.test.utils.RoundingUpUtils.roundingUpWithBaseDenominator100;
import static org.starlingbank.test.utils.TimeUtils.convertToStarlingTimestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavingsGoalServiceImpl implements SavingGoalService {

    private final StarlingProxy starlingProxy;
    private final AccountSavingGoalRepo accountSavingGoalRepo;
    private final RoundUpTransactionRepo roundUpTransactionRepo;

    private static RegisterAccountSavingGoalResponse mapFrom(final AccountSavingGoal accountSavingGoal) {

        return RegisterAccountSavingGoalResponse.builder()
                .savingGoalUuid(accountSavingGoal.getSavingGoalUuid())
                .accountUuid(accountSavingGoal.getAccountUuid())
                .name(accountSavingGoal.getGoalName())
                .currency(accountSavingGoal.getCurrency())
                .build();
    }

    private static RoundUpTransaction buildTransactionEntity(final TransactionFeed transactionFeed,
                                                             final AccountSavingGoal accountSavingGoal) {

        RoundUpTransaction roundUpTransaction = new RoundUpTransaction();
        roundUpTransaction.setTransactionUuid(transactionFeed.feedItemUid());
        roundUpTransaction.setRoundedUpAmount(roundingUpWithBaseDenominator100(transactionFeed.amount().minorUnits()));
        roundUpTransaction.setExecutedOn(Timestamp.valueOf(LocalDateTime.now()));
        roundUpTransaction.setAccountSavingGoal(accountSavingGoal);

        return roundUpTransaction;
    }

    private static AccountSavingGoal buildGoalEntity(final String savingGoalUuid,
                                                     final String goalName,
                                                     final String accountUuid,
                                                     final String currency) {

        AccountSavingGoal accountSavingGoal = new AccountSavingGoal();
        accountSavingGoal.setSavingGoalUuid(savingGoalUuid);
        accountSavingGoal.setAccountUuid(accountUuid);
        accountSavingGoal.setGoalName(goalName);
        accountSavingGoal.setCurrency(currency);
        accountSavingGoal.setRegisteredOn(Timestamp.valueOf(LocalDateTime.now()));

        return accountSavingGoal;
    }

    @Transactional
    public RegisterAccountSavingGoalResponse manageSavingGoal(final String token,
                                                              final String accountUid,
                                                              final AccountSavingGoalRequest accountSavingGoalRequest) {

        Optional<AccountSavingGoal> accountSavingGoalOptional = accountSavingGoalRepo.findByAccountUuidAndGoalName(accountUid, accountSavingGoalRequest.name());

        if (accountSavingGoalOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saving goal with account uid %s and goal name %s is already registered");
        } else {
            //Note:  Api to get account by Uid is not available, used the account balance api.
            //The below call will authorise if token is valid as well as account id belong to the token.
            //It will also give details about the currency.

            AccountBalance balance = starlingProxy.getAccountBalance(token, accountUid);
            String currency = balance.totalEffectiveBalance().currency();

            //Save/Update saving goal for the account in Starling bank
            Amount targetAmount = Amount.builder()
                    .currency(currency)
                    .minorUnits(accountSavingGoalRequest.targetAmount())
                    .build();

            String goalName = accountSavingGoalRequest.name();

            SavingGoalRequest savingGoalRequest = SavingGoalRequest.builder()
                    .name(goalName)
                    .currency(currency)
                    .description("Rounded up saving goal created/updated by SavingGoalApp")
                    .target(targetAmount)
                    .build();

            SavingsGoalResponse savingsGoalResponse = starlingProxy.saveOrUpdateSavingGoal(token, accountUid, savingGoalRequest);

            AccountSavingGoal accountSavingGoal =
                    accountSavingGoalRepo.save(buildGoalEntity(savingsGoalResponse.savingsGoalUid(), goalName, accountUid, currency));

            return mapFrom(accountSavingGoal);
        }
    }

    public void executeRoundingUpGoalFor(final String token,
                                         final String accountUid,
                                         final ExecuteGoalRequest executeGoalRequest) {

        List<AccountSavingGoal> accountSavingGoals = accountSavingGoalRepo.findAllByAccountUuid(accountUid);

        if (accountSavingGoals.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with uuid {} is not registered");
        } else {
            List<TransactionFeed> transactionFeed = getTransactionFeeds(token, accountUid, executeGoalRequest);

            if (!transactionFeed.isEmpty()) {
                for(AccountSavingGoal goal : accountSavingGoals) {
                    transactionFeedProcessing(token, accountUid, transactionFeed, goal);
                }
            }
        }
    }

    private List<TransactionFeed> getTransactionFeeds(final String token,
                                                      final String accountUid,
                                                      final ExecuteGoalRequest executeGoalRequest) {

        String startDate = convertToStarlingTimestamp(executeGoalRequest.fromDate().minusDays(executeGoalRequest.previousDays()).atStartOfDay());
        String endDate = convertToStarlingTimestamp(executeGoalRequest.fromDate().atTime(23, 59, 59));

        log.info("Getting transaction for account {} for startDate {} and endDate {}", accountUid, startDate, endDate);

        List<TransactionFeed> transactionFeed =
                Optional.ofNullable(starlingProxy.getSettledTransactionForAccountByDates(token, accountUid, startDate, endDate).feedItems()).orElseGet(List::of)
                        .stream()
                        .filter(feed -> feed.direction() == OUT)
                        .filter(feed -> feed.amount().minorUnits() % 100 > 0)
                        .toList();

        log.info("Received {} transaction feeds which expenses and requires rounding up", transactionFeed);

        return transactionFeed;
    }

    private void transactionFeedProcessing(final String token,
                                           final String accountUid,
                                           final List<TransactionFeed> transactionFeed,
                                           final AccountSavingGoal goal) {

        List<TransactionFeed> nonProcessedFeeds = transactionFeed.stream().
                filter(feed -> !roundUpTransactionRepo.existsByTransactionUuidAndAccountSavingGoal(feed.feedItemUid(), goal))
                .toList();

        long amount = 0;

        for (TransactionFeed feed : nonProcessedFeeds) {
            amount += feed.amount().minorUnits();
            roundUpTransactionRepo.save(buildTransactionEntity(feed, goal));
        }

        if (amount > 0) {
            topUpSavingGoal(token, accountUid, goal.getSavingGoalUuid(), goal.getCurrency(), amount);
        }
    }

    private void topUpSavingGoal(final String token,
                                 final String accountUuid,
                                 final String savingGoalId,
                                 final String currency,
                                 final long savingGoalAmount) {

        String transferUuid = UUID.randomUUID().toString();

        log.info("Topping up saving goal with transfer id {} with amount {} for saving goal uid {}", transferUuid, savingGoalAmount, savingGoalId);

        TransferRequestV2 transferRequest = TransferRequestV2.builder()
                .description("Topping up saving goal from app %s".formatted("SavingGoalApp"))
                .amount(Amount.builder().currency(currency).minorUnits(savingGoalAmount).build())
                .build();

        starlingProxy.topUpSavingGoal(token, accountUuid, savingGoalId, transferUuid, transferRequest);
    }
}
