package org.cardanofoundation.networkmonitoring.service;

import com.bloxbean.cardano.yaci.helper.model.Transaction;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.networkmonitoring.entity.TransactionAdoption;
import org.cardanofoundation.networkmonitoring.repository.TransactionAdoptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.cardanofoundation.networkmonitoring.model.Constants.TRANSACTION_ADOPTION_METADATA_KEY;
import static org.cardanofoundation.networkmonitoring.model.Constants.TRANSACTION_ADOPTION_TYPE;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "aggregation",
        name = "transaction-adoption.enabled",
        havingValue = "true"
)
public class TransactionAdoptionProcessor {

    @Value("${aggregation.transaction-adoption.wallets}")
    private final List<String> wallets;

    private final ObjectMapper objectMapper;

    private final TransactionAdoptionRepository transactionAdoptionRepository;

    private final TransactionChartService transactionChartService;

    private final AtomicReference<LocalDateTime> lastAggregationTime = new AtomicReference<>(LocalDateTime.MIN);

    private static final Duration DURATION = Duration.ofMinutes(5);

    @PostConstruct
    public void init() {
        log.info("INIT - Initialised");
    }

    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent transactionEvent) {

        transactionEvent
                .getTransactions()
                .forEach(transaction -> processTransaction(transaction, transactionEvent.getMetadata().getSlot()));

    }

    public void processTransaction(Transaction transaction, Long slot) {
        var outputs = transaction.getBody().getOutputs();

        for (int i = 0; i < outputs.size(); i++) {
            var transactionOutput = outputs.get(i);
            if (isRelevantAddress(transactionOutput.getAddress())) {
                var json = transaction.getAuxData().getMetadataJson();
                try {
                    var transactionAdoptionMetadataMap = objectMapper.readValue(json, TRANSACTION_ADOPTION_TYPE);

                    var transactionAdoptionMetadata = transactionAdoptionMetadataMap.get(TRANSACTION_ADOPTION_METADATA_KEY);

                    var absoluteSlot = Long.parseLong(transactionAdoptionMetadata.absoluteSlot());
                    var adoptionTimeInSeconds = slot - absoluteSlot;

                    var timestamp = Long.valueOf(transactionAdoptionMetadata.timestamp());

                    var transactionSubmissionTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

                    var transactionAdoption = TransactionAdoption.builder()
                            .txHash(transaction.getTxHash())
                            .outputIndex(i)
                            .absoluteSlot(absoluteSlot)
                            .slot(slot)
                            .adoptionTimeSeconds(adoptionTimeInSeconds)
                            .timestamp(transactionSubmissionTime)
                            .build();

                    transactionAdoptionRepository.save(transactionAdoption);

                    // FIXME: what if they stop sending tx for a long time?
                    if (Duration.between(lastAggregationTime.get(), transactionSubmissionTime).compareTo(DURATION) >= 0) {
                        transactionChartService.computeStats(transactionSubmissionTime);
                        lastAggregationTime.set(transactionSubmissionTime);
                    }

                } catch (Exception e) {
                    log.warn("error", e);
                }
            }
        }
    }


    private boolean isRelevantAddress(String address) {
        return address != null && wallets.contains(address);
    }

}
