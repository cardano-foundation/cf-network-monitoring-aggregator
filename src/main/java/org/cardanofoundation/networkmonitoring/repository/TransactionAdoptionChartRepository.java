package org.cardanofoundation.networkmonitoring.repository;

import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChart;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChartId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionAdoptionChartRepository extends JpaRepository<TransactionAdoptionChart, TransactionAdoptionChartId> {


}
