package org.cardanofoundation.networkmonitoring.entity;

import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_adoption")
@IdClass(UtxoId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAdoption {

    @Id
    private String txHash;
    @Id
    private Integer outputIndex;

    private Long adoptionTimeSeconds;

    // Timestamp as signalled from metadata
    private LocalDateTime timestamp;

    // Absolute slot matching Timestamp above as signalled in metadata
    private Long absoluteSlot;

    private Long slot;

}
