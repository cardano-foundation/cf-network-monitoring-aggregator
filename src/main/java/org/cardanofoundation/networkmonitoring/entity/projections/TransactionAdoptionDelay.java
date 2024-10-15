package org.cardanofoundation.networkmonitoring.entity.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface TransactionAdoptionDelay {

    @Value("#{target.TIME_BUCKET}")
    LocalDateTime getTimeBucket();

    @Value("#{target.AVG_ADOPTION_TIME_SECONDS}")
    Float getAvgAdoptionTimeSeconds();

}
