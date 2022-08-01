package org.starlingbank.test.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ROUNDED_UP_TRANSACTIONS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"TRANSACTION_ID", "ACCOUNT_SAVING_GOAL_ID"}))
@Data
public class RoundUpTransaction implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;
    @Column(name = "TRANSACTION_ID", nullable = false)
    String transactionUuid;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ACCOUNT_SAVING_GOAL_ID", referencedColumnName = "ID")
    AccountSavingGoal accountSavingGoal;
    @Column(name = "ROUNDED_UP_AMOUNT", nullable = false)
    long roundedUpAmount;
    @Column(name = "EXECUTED_ON", nullable = false)
    Timestamp executedOn;
}
