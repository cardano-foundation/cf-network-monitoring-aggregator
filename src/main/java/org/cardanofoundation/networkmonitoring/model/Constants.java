package org.cardanofoundation.networkmonitoring.model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

public interface Constants {

    String TRANSACTION_ADOPTION_METADATA_KEY = "1";

    TypeReference<HashMap<String, TransactionAdoptionMetadata>> TRANSACTION_ADOPTION_TYPE = new TypeReference<>() {
    };


}
