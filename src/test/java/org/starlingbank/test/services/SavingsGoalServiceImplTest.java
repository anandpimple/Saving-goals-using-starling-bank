package org.starlingbank.test.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.starlingbank.test.entities.AccountSavingGoal;
import org.starlingbank.test.entities.RoundUpTransaction;
import org.starlingbank.test.models.AccountSavingGoalRequest;
import org.starlingbank.test.models.ExecuteGoalRequest;
import org.starlingbank.test.models.RegisterAccountSavingGoalResponse;
import org.starlingbank.test.proxies.StarlingProxy;
import org.starlingbank.test.proxies.models.*;
import org.starlingbank.test.repositories.AccountSavingGoalRepo;
import org.starlingbank.test.repositories.RoundUpTransactionRepo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceImplTest {

    private static final String TOKEN = "token";
    private static final String ACCOUNT_ID = "accountId";
    private static final String SAVING_GOAL_UUID = "savingGoalUUID";
    private static final String GBP = "GBP";
    private static final String TEST_GOAL_NAME = "testGoalName";
    private static final String DESCRIPTION = "description";

    @Mock
    private StarlingProxy starlingProxyMock;
    @Mock
    private AccountSavingGoalRepo accountSavingGoalRepoMock;
    @Mock
    private RoundUpTransactionRepo roundUpTransactionRepoMock;

    @Mock
    private AccountSavingGoal accountSavingGoalMock;
    @Mock
    private RoundUpTransaction roundUpTransactionMock;

    @Captor
    private ArgumentCaptor<SavingGoalRequest> savingGoalRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<AccountSavingGoal> accountSavingGoalArgumentCaptor;
    @Captor
    private ArgumentCaptor<TransferRequestV2> transferRequestV2ArgumentCaptor;
    @Captor
    private ArgumentCaptor<RoundUpTransaction> roundUpTransactionArgumentCaptor;

    @InjectMocks
    private SavingsGoalServiceImpl underTest;

    @Test
    void givenProperDataWhenManageSavingGoalThenExpectedResponse() {

        AccountSavingGoalRequest accountSavingGoalRequest = AccountSavingGoalRequest.builder().name(TEST_GOAL_NAME).targetAmount(1000).build();

        when(starlingProxyMock.getAccountBalance(TOKEN, ACCOUNT_ID))
                .thenReturn(AccountBalance.builder().totalEffectiveBalance(Amount.builder().currency(GBP).minorUnits(5000).build()).build());

        when(starlingProxyMock.saveOrUpdateSavingGoal(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), savingGoalRequestArgumentCaptor.capture()))
                .thenReturn(new SavingsGoalResponse(DESCRIPTION, SAVING_GOAL_UUID, true, null));

        when(accountSavingGoalRepoMock.save(accountSavingGoalArgumentCaptor.capture())).thenReturn(accountSavingGoalMock);
        when(accountSavingGoalMock.getSavingGoalUuid()).thenReturn(SAVING_GOAL_UUID);

        RegisterAccountSavingGoalResponse response = underTest.manageSavingGoal(TOKEN, ACCOUNT_ID, accountSavingGoalRequest);
        RegisterAccountSavingGoalResponse expected = new RegisterAccountSavingGoalResponse(SAVING_GOAL_UUID, null, null, null);

        assertEquals(List.of(TOKEN, ACCOUNT_ID), stringArgumentCaptor.getAllValues());
        assertEquals(
                new SavingGoalRequest("Rounded up saving goal created/updated by SavingGoalApp", TEST_GOAL_NAME, GBP, new Amount(GBP, 1000), null),
                savingGoalRequestArgumentCaptor.getValue()
        );
        assertEquals(expected, response);
    }

    @Test
    void givenExecuteRoundingUpGoalForThenExpectedFlow() {

        when(accountSavingGoalRepoMock.findAllByAccountUuid(ACCOUNT_ID)).thenReturn(List.of(accountSavingGoalMock));
        when(starlingProxyMock.getSettledTransactionForAccountByDates(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture()))
                .thenReturn(new TransactionFeedList(List.of(new TransactionFeed(Amount.builder().minorUnits(1001).currency("GBP").build(),TransactionDirection.OUT,"feedItem1")), DESCRIPTION));
        when(starlingProxyMock.topUpSavingGoal(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), transferRequestV2ArgumentCaptor.capture()))
                .thenReturn(new SavingsGoalList(null, null));
        when(roundUpTransactionRepoMock.save(roundUpTransactionArgumentCaptor.capture())).thenReturn(roundUpTransactionMock);

        underTest.executeRoundingUpGoalFor(TOKEN, ACCOUNT_ID, new ExecuteGoalRequest(LocalDate.of(2022, 8, 1), 10));

        verify(roundUpTransactionRepoMock).existsByTransactionUuidAndAccountSavingGoal("feedItem1", accountSavingGoalMock);
        verify(roundUpTransactionRepoMock).save(any());

        assertEquals(8, stringArgumentCaptor.getAllValues().size());
    }
}