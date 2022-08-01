package org.starlingbank.test.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ACCOUNT_SAVINGS_GOAL")
@Data
public class AccountSavingGoal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    long id;
    @Column(name = "SAVING_GOAL_UUID", length = 64, nullable = false, unique = true)
    String savingGoalUuid;
    @Column(name = "GOAL_NAME", nullable = false)
    String goalName;
    @Column(name = "ACCOUNT_UUID", length = 64, nullable = false)
    String accountUuid;
    @Column(name = "CURRENCY", nullable = false)
    String currency;
    @Column(name = "REGISTERED_ON", nullable = false)
    Timestamp registeredOn;
}
