package org.cardanofoundation.networkmonitoring.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TransactionAdoptionChartId {

    private LocalDateTime timestamp;

    private IntervalType intervalType;

}
