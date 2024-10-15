package org.cardanofoundation.networkmonitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.networkmonitoring.entity.IntervalType;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChart;
import org.cardanofoundation.networkmonitoring.entity.projections.TransactionAdoptionDelay;
import org.cardanofoundation.networkmonitoring.repository.TransactionAdoptionChartRepository;
import org.cardanofoundation.networkmonitoring.repository.TransactionAdoptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static org.cardanofoundation.networkmonitoring.entity.IntervalType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionChartService {

    private final TransactionAdoptionRepository transactionAdoptionRepository;

    private final TransactionAdoptionChartRepository transactionAdoptionChartRepository;

    public void computeStats(LocalDateTime currentTime) {

        var startTime = System.currentTimeMillis();

        // Compute hourly stats first
        computeStats(() -> transactionAdoptionRepository.computeRecentTransactionAdoptionHourlyDelay(currentTime.minusDays(1)), 4, HOUR);

        // Compute daily stats from hourly
        computeStats(() -> transactionAdoptionRepository.computeTransactionAdoptionDelay(DAY.name(), HOUR.name(), currentTime.minusDays(4)), 2, DAY);

        // Compute weekly stats from daily
        computeStats(() -> transactionAdoptionRepository.computeTransactionAdoptionDelay(WEEK.name(), DAY.name(), currentTime.minusWeeks(4)), 2, WEEK);

        // Compute monthly stats from daily
        computeStats(() -> transactionAdoptionRepository.computeTransactionAdoptionDelay(MONTH.name(), WEEK.name(), currentTime.minusMonths(4)), 2, MONTH);

        log.info("computeStats executed in {}ms", System.currentTimeMillis() - startTime);

    }

    private void computeStats(Supplier<List<TransactionAdoptionDelay>> fetchStatistics, int numSamplesToUpdate, IntervalType intervalType) {
        List<TransactionAdoptionDelay> transactionAdoptionDelays = fetchStatistics.get();
        var recentTransactionAdoptionDelays = transactionAdoptionDelays
                .stream()
                .sorted(Comparator.comparing(TransactionAdoptionDelay::getTimeBucket))
                .toList()
                .reversed();
        recentTransactionAdoptionDelays
                .stream()
                .limit(numSamplesToUpdate)
                .filter(delay -> delay.getTimeBucket() != null)
                .forEach(transactionAdoptionDelay -> {
                    var transactionAdoptionChart = TransactionAdoptionChart
                            .builder()
                            .timestamp(transactionAdoptionDelay.getTimeBucket())
                            .avgAdoptionTimeSeconds(transactionAdoptionDelay.getAvgAdoptionTimeSeconds())
                            .intervalType(intervalType)
                            .build();
                    transactionAdoptionChartRepository.save(transactionAdoptionChart);
                });
    }

}
