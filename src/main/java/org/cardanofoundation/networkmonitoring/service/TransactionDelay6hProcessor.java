package org.cardanofoundation.networkmonitoring.service;

import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.networkmonitoring.model.TransactionAdoptionMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "transaction-delay-6h.enabled",
        havingValue = "true"
)
public class TransactionDelay6hProcessor {

    @Value("${explorer.aggregation.transaction-adoption-6h.wallets}")
    private final List<String> wallets;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("INIT - Starting");
    }

    @EventListener
    public void handleTransactionEvent(TransactionEvent transactionEvent) {
        TypeReference<HashMap<String, TransactionAdoptionMetadata>> typeRef = new TypeReference<HashMap<String, TransactionAdoptionMetadata>>() {
        };

        try {
            transactionEvent
                    .getTransactions()
                    .forEach(transaction -> transaction.getBody()
                            .getOutputs()
                            .forEach(transactionOutput -> {
                                        if (isRelevantAddress(transactionOutput.getAddress())) {
                                            var json = transaction.getAuxData().getMetadataJson();
                                            try {
                                                var transactionAdoptionMetadata = objectMapper.readValue(json, typeRef);
                                            } catch (JsonProcessingException e) {
                                                log.warn("error", e);
                                            }
                                        }
                                    }
                            )
                    );
        } catch (Exception e) {
            //
        }

    }


    private boolean isRelevantAddress(String address) {
        return address != null && wallets.contains(address);
    }

}
