package org.cardanofoundation.networkmonitoring.controller;

import lombok.RequiredArgsConstructor;
import org.cardanofoundation.networkmonitoring.entity.IntervalType;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoptionChart;
import org.cardanofoundation.networkmonitoring.model.TransactionAdoptionDelay;
import org.cardanofoundation.networkmonitoring.model.TransactionAdoptionPeriod;
import org.cardanofoundation.networkmonitoring.repository.TransactionAdoptionChartRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/network-monitoring/transaction-adoption")
@RequiredArgsConstructor
public class TransactionAdoptionController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TransactionAdoptionChartRepository transactionAdoptionChartRepository;

    @GetMapping
    public ResponseEntity<List<TransactionAdoptionDelay>> get(@RequestParam(name = "period", required = false, defaultValue = "THREE_DAY") TransactionAdoptionPeriod transactionAdoptionPeriod) {

        var result = switch (transactionAdoptionPeriod) {
            case THREE_DAY -> {
                var from = LocalDateTime.now(ZoneOffset.UTC).minusDays(3);
                yield transactionAdoptionChartRepository.findByIntervalTypeAndTimestampAfter(IntervalType.HOUR, from);
            }
            case ONE_WEEK -> {
                var from = LocalDateTime.now(ZoneOffset.UTC).minusWeeks(1);
                yield transactionAdoptionChartRepository.findByIntervalTypeAndTimestampAfter(IntervalType.HOUR, from);
            }
            case ONE_MONTH -> {
                var from = LocalDateTime.now(ZoneOffset.UTC).minusMonths(1);
                yield transactionAdoptionChartRepository.findByIntervalTypeAndTimestampAfter(IntervalType.DAY, from);
            }
            case ONE_YEAR -> {
                var from = LocalDateTime.now(ZoneOffset.UTC).minusYears(1);
                yield transactionAdoptionChartRepository.findByIntervalTypeAndTimestampAfter(IntervalType.MONTH, from);
            }
            default -> {
                var from = LocalDateTime.MIN;
                yield transactionAdoptionChartRepository.findByIntervalTypeAndTimestampAfter(IntervalType.YEAR, from);
            }
        };

        var dataPoints = result.stream()
                .sorted(Comparator.comparing(TransactionAdoptionChart::getTimestamp))
                .map(transactionAdoptionChart -> new TransactionAdoptionDelay(transactionAdoptionChart.getTimestamp().format(DATE_TIME_FORMATTER),
                        transactionAdoptionChart.getAvgAdoptionTimeSeconds()))
                .toList();

        return ResponseEntity.ok(dataPoints);
    }

}
