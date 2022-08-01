package org.starlingbank.test.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.starlingbank.test.proxies.models.*;

@FeignClient(name = "starlingBank", url = "${starling.api.base.url}")
public interface StarlingProxy {
    @GetMapping("/accounts/{accountUuid}")
    String checkAccount(@RequestHeader("Authorization") String authHeader);

    @GetMapping(value = "/feed/account/{accountUid}/settled-transactions-between", produces = MediaType.APPLICATION_JSON_VALUE)
    TransactionFeedList getSettledTransactionForAccountByDates(@RequestHeader("Authorization") String authHeader,
                                                               @PathVariable("accountUid") String accountUid,
                                                               @RequestParam("minTransactionTimestamp") String minDate,
                                                               @RequestParam("maxTransactionTimestamp") String maxDate);

    @PutMapping("/account/{accountUid}/savings-goals")
    SavingsGoalResponse saveOrUpdateSavingGoal(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable("accountUid") String accountUid,
                                               @RequestBody SavingGoalRequest savingGoalRequest);

    @PutMapping("/account/{accountUid}/savings-goals/{savingGoalUid}/add-money/{transferUid}")
    SavingsGoalList topUpSavingGoal(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable("accountUid") String accountUid,
                                    @PathVariable("savingGoalUid") String savingGoalUid,
                                    @PathVariable("transferUid") String transferUid,
                                    @RequestBody TransferRequestV2 transferRequest);

    @GetMapping("/accounts/{accountUid}/balance")
    AccountBalance getAccountBalance(@RequestHeader("Authorization") String authHeader,
                                     @PathVariable("accountUid") String accountUid);
}
