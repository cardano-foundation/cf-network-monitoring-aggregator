CREATE TABLE IF NOT EXISTS transaction_adoption (
    tx_hash TEXT,
    output_index BIGINT,
    adoption_time_seconds BIGINT NOT NULL,
    "timestamp" TIMESTAMP WITHOUT TIME ZONE,
    absolute_slot BIGINT NOT NULL,
    slot  BIGINT NOT NULL,
    PRIMARY KEY (tx_hash, output_index)
);

CREATE INDEX IF NOT EXISTS transaction_adoption_slot_idx ON transaction_adoption (slot DESC);

CREATE TABLE IF NOT EXISTS transaction_adoption_chart (
    "timestamp" TIMESTAMP WITHOUT TIME ZONE,
    interval_type TXT,
    avg_adoption_time_seconds DOUBLE PRECISION NOT NULL,
    PRIMARY KEY ("timestamp", interval_type)
);
