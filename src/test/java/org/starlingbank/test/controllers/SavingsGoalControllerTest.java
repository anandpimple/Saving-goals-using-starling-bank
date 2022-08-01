package org.starlingbank.test.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.starlingbank.test.models.AccountSavingGoalRequest;
import org.starlingbank.test.models.ExecuteGoalRequest;
import org.starlingbank.test.models.RegisterAccountSavingGoalResponse;
import org.starlingbank.test.services.SavingGoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SavingsGoalController.class)
class SavingsGoalControllerTest {
    @MockBean
    private SavingGoalService savingGoalServiceMock;

    @Captor
    private ArgumentCaptor<AccountSavingGoalRequest> savingGoalRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<ExecuteGoalRequest> executeGoalRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenAccountIdNotUidWhenCallAddSavingGoalEndpointThenBadRequestStatus() throws Exception {
        mockMvc.perform(post("/account/asasasasas/saving-goal")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "token")
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(savingGoalServiceMock);
    }

    @Test
    void givenAccountIdIsUidButEmptyBodyWhenCallAddSavingGoalEndpointThenBadRequestStatus() throws Exception {
        mockMvc.perform(post("/account/bbebc062-5216-465c-bf34-81b3191135a4/saving-goal")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "token")
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(savingGoalServiceMock);
    }

    @Test
    void givenProperRequestWithoutHeaderWhenCallAddSavingGoalEndpointThenBadRequestStatus() throws Exception {

        mockMvc.perform(post("/account/bbebc062-5216-465c-bf34-81b3191135a4/saving-goal")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                            "name" : "Test saving goal",
                            "targetAmount": 10000
                        }
                        """)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(savingGoalServiceMock);

    }

    @Test
    void givenAccountIdIsUidAndProperDataWhenCallAddSavingGoalEndpointThenSuccessStatus() throws Exception {

        RegisterAccountSavingGoalResponse response = new RegisterAccountSavingGoalResponse("savingGoalUuid", "name", "accountUuid", "GBP");
        when(savingGoalServiceMock.manageSavingGoal(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), savingGoalRequestArgumentCaptor.capture()))
                .thenReturn(response);

        mockMvc.perform(post("/account/bbebc062-5216-465c-bf34-81b3191135a4/saving-goal")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "token")
                        .content("""
                                {
                                    "name" : "Test saving goal",
                                    "targetAmount": 10000
                                }
                                """)
                ).andExpect(status().isOk())
                .andExpect(content().string("{\"savingGoalUuid\":\"savingGoalUuid\",\"name\":\"name\",\"accountUuid\":\"accountUuid\",\"currency\":\"GBP\"}"));

        assertEquals(List.of("token", "bbebc062-5216-465c-bf34-81b3191135a4"), stringArgumentCaptor.getAllValues());

        List<String> allCapturedStringValues = stringArgumentCaptor.getAllValues();
        verify(savingGoalServiceMock).manageSavingGoal(allCapturedStringValues.get(0), allCapturedStringValues.get(1), savingGoalRequestArgumentCaptor.getValue());

    }

    @Test
    void givenAccountIdIsUidAndProperDataWhenCallExecuteRoundingUpGoalForThenSuccessStatus() throws Exception {

        RegisterAccountSavingGoalResponse response = new RegisterAccountSavingGoalResponse("savingGoalUuid", "name", "accountUuid", "GBP");
        doNothing().when(savingGoalServiceMock).executeRoundingUpGoalFor(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), executeGoalRequestArgumentCaptor.capture());

        mockMvc.perform(put("/account/bbebc062-5216-465c-bf34-81b3191135a4/execute-round-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "token")
                .content("""
                        {
                            "fromDate" : "2022-07-31",
                            "previousDays": 20
                        }
                        """)
        ).andExpect(status().isOk());

        assertEquals(List.of("token", "bbebc062-5216-465c-bf34-81b3191135a4"), stringArgumentCaptor.getAllValues());

        List<String> allCapturedStringValues = stringArgumentCaptor.getAllValues();
        verify(savingGoalServiceMock).executeRoundingUpGoalFor(allCapturedStringValues.get(0), allCapturedStringValues.get(1), executeGoalRequestArgumentCaptor.getValue());

    }
}