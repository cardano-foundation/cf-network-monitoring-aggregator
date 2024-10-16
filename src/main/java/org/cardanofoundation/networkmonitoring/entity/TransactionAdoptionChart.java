package org.cardanofoundation.networkmonitoring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_adoption_chart")
@IdClass(TransactionAdoptionChartId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAdoptionChart {

    @Id
    private LocalDateTime timestamp;

    @Id
    @Enumerated(EnumType.STRING)
    private IntervalType intervalType;

    private Double avgAdoptionTimeSeconds;

}
