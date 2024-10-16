package org.cardanofoundation.networkmonitoring.repository;

import org.cardanofoundation.networkmonitoring.entity.IntervalType;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChart;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChartId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionAdoptionChartRepository extends JpaRepository<TransactionAdoptionChart, TransactionAdoptionChartId> {

    List<TransactionAdoptionChart> findByIntervalTypeAndTimestampAfter(IntervalType intervalType, LocalDateTime from);

}
