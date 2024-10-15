package org.cardanofoundation.networkmonitoring.repository;

import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoption;
import org.cardanofoundation.networkmonitoring.entity.projections.TransactionAdoptionDelay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionAdoptionRepository extends JpaRepository<TransactionAdoption, UtxoId> {

    void deleteBySlotGreaterThan(long slot);

    @Query(value = """
            WITH latest_tx_adoption AS
                (SELECT "timestamp",
                        adoption_time_seconds,
                        round(avg(adoption_time_seconds) over (ORDER BY "timestamp" ROWS BETWEEN 4 PRECEDING AND CURRENT ROW),1) AS avg_delay_1h
                 FROM transaction_adoption
                 WHERE "timestamp" > :startTime
                 ORDER BY "timestamp" DESC)
            SELECT date_trunc('hour', "timestamp") AS time_bucket, avg(avg_delay_1h) AS avg_adoption_time_seconds
            FROM latest_tx_adoption GROUP BY time_bucket ORDER BY time_bucket DESC
            """, nativeQuery = true)
    List<TransactionAdoptionDelay> computeRecentTransactionAdoptionHourlyDelay(LocalDateTime startTime);

    @Query(value = """
            SELECT date_trunc(:groupingInterval, "timestamp") AS time_bucket, avg(avg_adoption_time_seconds) AS avg_adoption_time_seconds
            FROM transaction_adoption_chart
            WHERE interval_type = :intervalSelectionType
              AND "timestamp" > :startTime
            GROUP BY time_bucket ORDER BY time_bucket DESC
            """, nativeQuery = true)
    List<TransactionAdoptionDelay> computeTransactionAdoptionDelay(String groupingInterval, String intervalSelectionType, LocalDateTime startTime);


}
